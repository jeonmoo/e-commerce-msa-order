package com.ecommerce.order.domain.entity;

import com.ecommerce.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long userId;

    @Setter
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems;

    @Column
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Setter
    @Column
    private BigDecimal totalFinalPrice;

    @Setter
    @Column
    private BigDecimal totalOriginPrice;

    @Column
    @Setter
    private BigDecimal totalDiscountPrice;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Order(Long userId, List<OrderItem> orderItems,
                 String address, BigDecimal totalFinalPrice, BigDecimal totalOriginPrice,
                 BigDecimal totalDiscountPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.orderItems = orderItems;
        this.orderStatus = OrderStatus.PENDING;
        this.address = address;
        this.totalFinalPrice = totalFinalPrice;
        this.totalOriginPrice = totalOriginPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
