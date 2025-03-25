package com.ecommerce.order.domain.service;

import com.ecommerce.order.common.GlobalException;
import com.ecommerce.order.common.exceptionCode.OrderExceptionCode;
import com.ecommerce.order.common.exceptionCode.ProductExceptionCode;
import com.ecommerce.order.common.exceptionCode.UserExceptionCode;
import com.ecommerce.order.external.product.client.ProductClient;
import com.ecommerce.order.external.product.dto.ProductResponse;
import com.ecommerce.order.domain.dto.OrderRequest;
import com.ecommerce.order.domain.dto.OrderResponse;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.enums.OrderStatus;
import com.ecommerce.order.domain.mapper.OrderItemMapper;
import com.ecommerce.order.domain.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderProcessService {

    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItem> orderItemResponse = order.getOrderItems()
                .stream()
                .map(OrderItemMapper.INSTANCE::toOrderItemResponse)
                .toList();

        OrderResponse orderResponse = OrderMapper.INSTANCE.toOrderResponse(order);
        orderResponse.setOrderItems(orderItemResponse);
        return orderResponse;
    }

    protected Order initOrder(OrderRequest request, List<ProductResponse> products) {
        Order order = OrderMapper.INSTANCE.toOrder(request);

        List<OrderItem> orderItems = initOrderItem(request, products);
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);
        initPriceInOrder(order);
        return order;
    }

    private List<OrderItem> initOrderItem(OrderRequest request, List<ProductResponse> products) {
        Map<Long, ProductResponse> productMap = products.stream()
                .collect(Collectors.toMap(ProductResponse::getId, product -> product));

        return request.getOrderItems().stream()
                .map(itemRequest -> {
                    ProductResponse product = productMap.get(itemRequest.getProductId());
                    return OrderItem.builder()
                            .quantity(itemRequest.getQuantity())
                            .originPrice(product.getOriginPrice())
                            .finalPrice(product.getFinalPrice())
                            .productId(product.getId())
                            .build();
                })
                .toList();
    }

    @Transactional
    protected List<ProductResponse> getProductInIds(OrderRequest request) {
        List<Long> productIds = request.getOrderItems().stream()
                .map(OrderRequest.OrderItem::getProductId)
                .toList();

        List<ProductResponse> products = productClient.getProductInIds(productIds);
        if (products.size() != request.getOrderItems().size()) {
            throw new GlobalException(ProductExceptionCode.NOT_FOUND_PRODUCT);
        }
        return products;
    }

    protected void checkProductStock(List<OrderRequest.OrderItem> orderItems, List<ProductResponse> products) {
        Map<Long, ProductResponse> productMap = products.stream()
                .collect(Collectors.toMap(ProductResponse::getId, product -> product));
        orderItems.forEach(item -> {
            ProductResponse product = productMap.get(item.getProductId());
            Integer stock = product.getQuantity();
            if (item.getQuantity() > stock) {
                throw new GlobalException(ProductExceptionCode.OUT_OF_STOCK);
            }
        });
    }

    @Transactional
    protected void pay(Order order) {
        kafkaTemplate.send("payment-pay", order);
    }

    @Transactional
    protected void initPriceInOrder(Order order) {
        BigDecimal totalOriginPrice = order.getOrderItems().stream()
                .map(OrderItem::getOriginPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscountPrice = order.getOrderItems().stream()
                .map(OrderItem::getDiscountPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFinalPrice = order.getOrderItems().stream()
                .map(OrderItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalOriginPrice(totalOriginPrice);
        order.setTotalDiscountPrice(totalDiscountPrice);
        order.setTotalFinalPrice(totalFinalPrice);
    }

    @Transactional
    protected void reduceStock(List<ProductResponse> products) {
        kafkaTemplate.send("product-reduceStock", products);
    }

    @Transactional
    protected void completeOrder(Order order) {
        order.setOrderStatus(OrderStatus.COMPLETE);
    }

    protected void checkOrderPending(Order order) {
        OrderStatus status = order.getOrderStatus();
        if (!OrderStatus.PENDING.equals(status)) {
            throw new GlobalException(OrderExceptionCode.NOT_PENDING);
        }
    }
}
