package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", updatable = false, nullable = false)
    private Integer eventId;

    @Column(length = 255)
    private String title;

    @Column(name = "s_time")
    private LocalDateTime startTime;

    @Column(name = "e_time")
    private LocalDateTime endTime;
    
    @Column(name = "points_distributed", nullable = false)
    @Builder.Default
    private Boolean pointsDistributed = false;
}
