package com.ecommerce.order.domain.mapper;

import com.ecommerce.order.domain.dto.OrderRequest;
import com.ecommerce.order.domain.dto.OrderResponse;
import com.ecommerce.order.domain.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItem toOrderItem(OrderRequest.OrderItem request);

    OrderResponse.OrderItem toOrderItemResponse(OrderItem orderItem);

}
