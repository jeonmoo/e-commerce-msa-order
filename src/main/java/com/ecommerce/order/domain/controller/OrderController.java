package com.ecommerce.order.domain.controller;

import com.ecommerce.order.common.ApiResponse;
import com.ecommerce.order.domain.dto.OrderRequest;
import com.ecommerce.order.domain.dto.OrderResponse;
import com.ecommerce.order.domain.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> registerOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.registerOrder(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(@PathVariable Long id) {
        OrderResponse result = orderService.completeOrder(id);
        return ApiResponse.success(result);
    }

}
