package com.ecommerce.order.domain.mapper;

import com.ecommerce.order.domain.dto.OrderRequest;
import com.ecommerce.order.domain.dto.OrderResponse;
import com.ecommerce.order.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toOrder(OrderRequest request);

    OrderResponse toOrderResponse(Order order);
}
