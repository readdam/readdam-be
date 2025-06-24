package com.kosta.readdam.dto;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassReview;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassReviewDto {

    private Integer classReviewId;
    private Integer classId;
    private String username;
    private String content;
    private LocalDateTime regDate;
    private Boolean isHide;
    private Integer rating;
    private String img;
    
    private String title;            
    private String mainImg;          
    private LocalDateTime round1Date; 
    private String round1PlaceLoc;    
    private String tag1;
    private String tag2;
    private String tag3;

    public ClassReview toEntity(User user, ClassEntity classEntity) {
        return ClassReview.builder()
                .classReviewId(classReviewId)
                .classEntity(classEntity)
                .user(user)
                .content(content)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .isHide(isHide != null ? isHide : false)
                .rating(rating)
                .img(img)
                .build();
    }
}
