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
    private String username;
    private PaymentStatus paymentStatus;
    private String paymentKey;
    private LocalDateTime approvedAt;
    private String paymentMethod;
    private int price;
    
    public Order toEntity(User user) {
        return Order.builder()
            .orderId(orderId) 
            .user(user)
            .paymentStatus(paymentStatus)
            .paymentKey(paymentKey)
            .approvedAt(approvedAt)
            .paymentMethod(paymentMethod)
            .price(price)
            .build();
    }


}
