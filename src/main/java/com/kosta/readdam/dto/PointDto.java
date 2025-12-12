package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.Point;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointDto {

    private Long pointId;
    private String username;
    private Integer point;
    private LocalDateTime date;
    private String reason;
    private Long orderId;

    public Point toEntity(User user, Order order) {
        return Point.builder()
                .pointId(pointId)
                .user(user)
                .point(point)
                .date(date)
                .reason(reason)
                .order(order)
                .build();
    }

}
