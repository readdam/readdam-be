package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.Order;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long orderId;
    private String orderUuid;       // Toss 요청용 UUID
    private String orderName;       // 상품 이름 (ex. 500P 충전)
    private String username;
    private PaymentStatus paymentStatus;
    private String paymentKey;
    private String paymentMethod;
    private int price;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String failReason;

    public Order toEntity(User user) {
        return Order.builder()
            .orderId(orderId) 
            .orderUuid(orderUuid)
            .orderName(orderName)
            .user(user)
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
