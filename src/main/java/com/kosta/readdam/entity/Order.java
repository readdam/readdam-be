package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kosta.readdam.dto.OrderDto;
import com.kosta.readdam.entity.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", updatable = false, nullable = false)
    private Long orderId;

    @Column(name = "order_uuid", unique = true, nullable = false, length = 100)
    private String orderUuid;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", columnDefinition = "ENUM('PENDING','APPROVED','CANCELLED')", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "fail_reason", length = 255)
    private String failReason;

    public OrderDto toDto() {
        return OrderDto.builder()
            .orderId(orderId)
            .orderUuid(orderUuid)
            .orderName(orderName)
            .username(user.getUsername())
            .paymentStatus(paymentStatus)
            .paymentKey(paymentKey)
            .paymentMethod(paymentMethod)
            .price(price)
            .requestedAt(requestedAt)
            .approvedAt(approvedAt)
            .failReason(failReason)
            .build();
    }
}
