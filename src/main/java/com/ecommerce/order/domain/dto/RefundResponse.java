package com.ecommerce.order.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RefundResponse {

    private Long id;
    private BigDecimal refundAmount;
    private String reason;
    private LocalDateTime createdAt;
}
