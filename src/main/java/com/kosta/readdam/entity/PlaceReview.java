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

import com.kosta.readdam.dto.PlaceReviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    public PlaceReviewDto toDto() {
        return PlaceReviewDto.builder()
                .placeReviewId(this.placeReviewId)
                .content(this.content)
                .rating(this.rating)
                .username(this.user.getUsername())
                .nickname(this.user.getNickname())
                .regTime(this.regTime)
                .placeId(this.place.getPlaceId())
                .isHide(this.isHide)
                .build();
    }

}
