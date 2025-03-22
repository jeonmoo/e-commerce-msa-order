package com.ecommerce.order.domain.order.entity;

import com.ecommerce.order.domain.order.enums.OrderStatus;
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

@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "orderItem")
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Setter
    private Long productId;

    @Setter
    @Column
    private OrderStatus orderStatus;

    @Column
    private Integer quantity;

    @Column
    private BigDecimal originPrice;

    @Setter
    @Column
    private BigDecimal finalPrice;

    @Column
    private BigDecimal discountPrice;

//    @OneToMany(mappedBy = "orderItem")
//    private List<OrderItemDiscount> discounts;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public OrderItem(Order order, Long productId, OrderStatus orderStatus,
                     Integer quantity, BigDecimal originPrice, BigDecimal finalPrice,
                     BigDecimal discountPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.order = order;
        this.productId = productId;
        this.orderStatus = orderStatus;
        this.quantity = quantity;
        this.originPrice = originPrice;
        this.finalPrice = finalPrice;
        this.discountPrice = discountPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
