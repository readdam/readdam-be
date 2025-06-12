package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;

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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
