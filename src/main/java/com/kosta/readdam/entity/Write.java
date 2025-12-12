package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.kosta.readdam.dto.WriteDto;

import lombok.*;

@Entity
@Table(name = "`write`") // write는 예약어라 백틱 필요
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Write {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "write_id", updatable = false, nullable = false)
    private Integer writeId;

    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(nullable = false, length = 50)
    private String type;

    @Column(length = 50)
    private String tag1;

    @Column(length = 50)
    private String tag2;

    @Column(length = 50)
    private String tag3;

    @Column(length = 50)
    private String tag4;

    @Column(length = 50)
    private String tag5;

    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String img;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "view_cnt", nullable = false)
    private int viewCnt;

    @Column(name = "is_hide", nullable = false)
    private boolean isHide;
    
    @Column(name = "like_cnt", nullable = false)
    @Builder.Default
    private Integer likeCnt = 0;  // 좋아요 수 기본값 0으로 설정
    
    @Column(name = "comment_cnt", nullable = false)
    @Builder.Default
    private Integer commentCnt = 0;
   
    
    public WriteDto toDto() {
        return WriteDto.builder()
                .writeId(writeId)
                .title(title)
                .writeType(type)
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .tag4(tag4)
                .tag5(tag5)
                .regDate(regDate)
                .endDate(endDate)
                .content(content)
                .img(img)
                .username(user != null ? user.getUsername() : null)
                .nickname(user != null ? user.getNickname() : null)          
                .profileImg(user != null ? user.getProfileImg() : null)      
                .introduce(user != null ? user.getIntroduce() : null)       
                .viewCnt(viewCnt)
                .isHide(isHide)
                .likeCnt(likeCnt)
                .commentCnt(commentCnt)
                .build();
    }
    
}
