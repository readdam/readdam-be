package com.kosta.readdam.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "place_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_review_id", nullable = false, updatable = false)
    private Integer placeReviewId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "reg_time", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "is_hide")
    private Boolean isHide;
}
