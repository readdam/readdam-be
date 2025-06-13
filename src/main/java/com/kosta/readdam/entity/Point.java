package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.kosta.readdam.dto.PointDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "point")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id", updatable = false, nullable = false)
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "point", nullable = false)
    private int point;

    @CreationTimestamp
    @Column(name = "date", updatable = false, nullable = false)
    private LocalDateTime date;

    @Column(name = "reason", length = 255)
    private String reason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    
    public PointDto toDto() {
        return PointDto.builder()
            .pointId(pointId)
            .point(point)
            .date(date)
            .reason(reason)
            .username(user.getUsername())
            .orderId(order != null ? order.getOrderId() : null)
            .build();
    }

}
