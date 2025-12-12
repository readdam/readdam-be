package com.kosta.readdam.entity;

import com.kosta.readdam.dto.ClassReviewDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_review_id", nullable = false, updatable = false)
    private Integer classReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;

    @Column(nullable = false)
    private Integer rating;

    private String img;

    public ClassReviewDto toDto() {
    	ClassEntity c = this.classEntity;
        return ClassReviewDto.builder()
                .classReviewId(classReviewId)
                .classId(classEntity.getClassId())
                .username(user.getUsername())
                .content(content)
                .regDate(regDate)
                .isHide(isHide)
                .rating(rating)
                .img(img)
                .title(c.getTitle())
                .mainImg(c.getMainImg())
                .round1Date(c.getRound1Date())
                .round1PlaceLoc(c.getRound1PlaceLoc())
                .tag1(c.getTag1())
                .tag2(c.getTag2())
                .tag3(c.getTag3())
                .build();
    }
}
