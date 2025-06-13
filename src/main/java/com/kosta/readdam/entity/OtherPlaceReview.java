package com.kosta.readdam.entity;

import com.kosta.readdam.dto.OtherPlaceReviewDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "other_place_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlaceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "other_place_review_id", nullable = false, updatable = false)
    private Integer otherPlaceReviewId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "reg_time", nullable = false)
    private LocalDateTime regTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_place_id", nullable = false)
    private OtherPlace otherPlace;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;

    // 엔티티 → DTO
    public OtherPlaceReviewDto toDto() {
        return OtherPlaceReviewDto.builder()
                .otherPlaceReviewId(otherPlaceReviewId)
                .content(content)
                .rating(rating)
                .username(user.getUsername())
                .regTime(regTime)
                .otherPlaceId(otherPlace.getOtherPlaceId())
                .isHide(isHide)
                .build();
    }
}
