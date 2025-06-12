
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

import com.kosta.readdam.entity.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`order`") // order는 MySQL 예약어이므로 반드시 백틱으로 감쌈
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

    // 주문한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", columnDefinition = "ENUM('PENDING','APPROVED','CANCELLED')", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "price", nullable = false)
    private int price;
}
