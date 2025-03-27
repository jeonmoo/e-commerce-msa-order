package com.ecommerce.order.domain.service;

import com.ecommerce.order.common.GlobalException;
import com.ecommerce.order.common.exceptionCode.OrderExceptionCode;
import com.ecommerce.order.external.product.dto.ProductResponse;
import com.ecommerce.order.domain.dto.OrderRequest;
import com.ecommerce.order.domain.dto.OrderResponse;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.mapper.OrderMapper;
import com.ecommerce.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderProcessService orderProcessService;
    private final OrderRepository orderRepository;


    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new GlobalException(OrderExceptionCode.NOT_FOUND_ORDER));
    }

    public OrderResponse getOrder(Long id) {
        Order order = findOrder(id);
        return  orderProcessService.toResponse(order);
    }

    @Transactional
    public OrderResponse registerOrder(OrderRequest request) {
        List<ProductResponse> products = orderProcessService.getProductInIds(request);
        orderProcessService.checkProductStock(request.getOrderItems(), products);
        orderProcessService.reduceStock(products);

        Order order = orderProcessService.initOrder(request, products);
        Order savedOrder = orderRepository.save(order);
        orderProcessService.pay(savedOrder);

        return orderProcessService.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse completeOrder(Long orderId) {
        Order order = findOrder(orderId);

        orderProcessService.checkOrderPending(order);
        orderProcessService.completeOrder(order);
        return OrderMapper.INSTANCE.toOrderResponse(order);
    }
}
