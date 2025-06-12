package com.kosta.readdam.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "place_time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_time_id", updatable = false, nullable = false)
    private Integer placeTimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_room_id", nullable = false)
    private PlaceRoom placeRoom;

    @Column(name = "is_weekend", nullable = false)
    private Boolean isWeekend;

    @Column(nullable = false)
    private Boolean active;

    @Column(length = 50)
    private String time;
}
