package com.kosta.readdam.dto;

import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import lombok.*;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WriteDto {

    private Integer writeId;
    private String title;
    private String type;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime regDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    private String content;
    private String img;
    private String username;
    private int viewCnt;
    private boolean isHide;
    private String visibility; // "public" 또는 "private"
    private String nickname; //상세페이지용 추가
    private String profileImg; //상세페이지용 추가
    private String introduce; //상세페이지용 추가
    private Integer likeCnt;
    private Integer commentCnt;
    private boolean isNeedReview;
    private boolean isReviewDeadlinePassed;
    private String thumbnailUrl; //북커버 이미지용

    public Write toEntity(User user) {
        return Write.builder()
                .writeId(writeId)
                .title(title)
                .type(type)
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .tag4(tag4)
                .tag5(tag5)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .endDate(endDate)
                .content(content)
                .img(img)
                .user(user)
                .viewCnt(viewCnt)
                .isHide("private".equals(this.visibility))
                .build();
    }
    
    public static WriteDto from(Write write) {
        User user = write.getUser(); // 글상세 작성자 정보
    	
        return WriteDto.builder()
            .writeId(write.getWriteId())
            .title(write.getTitle())
            .type(write.getType())
            .tag1(write.getTag1())
            .tag2(write.getTag2())
            .tag3(write.getTag3())
            .tag4(write.getTag4())
            .tag5(write.getTag5())
            .regDate(write.getRegDate())
            .endDate(write.getEndDate())
            .isNeedReview(write.getEndDate() != null)
            .isReviewDeadlinePassed(
                write.getEndDate() != null && write.getEndDate().isBefore(LocalDateTime.now())
            )
            .content(write.getContent())
            .img(write.getImg())
            .viewCnt(write.getViewCnt())
            .visibility(write.isHide() ? "private" : "public")
            .likeCnt(write.getLikeCnt())
            .commentCnt(write.getCommentCnt())
            
            // 작성자 정보 추가 
            .username(user != null ? user.getUsername() : null)
            .nickname(user != null ? user.getNickname() : null)
            .profileImg(user != null ? user.getProfileImg() : null)
            .introduce(user != null ? user.getIntroduce() : null)
            
            .build();
    }

	@Override
	public String toString() {
		return "WriteDto [writeId=" + writeId + ", title=" + title + ", type=" + type + ", tag1=" + tag1 + ", tag2="
				+ tag2 + ", tag3=" + tag3 + ", tag4=" + tag4 + ", tag5=" + tag5 + ", regDate=" + regDate + ", endDate="
				+ endDate + ", content=" + content + ", img=" + img + ", username=" + username + ", viewCnt=" + viewCnt
				+ ", isHide=" + isHide + ", visibility=" + visibility + ", nickname=" + nickname + ", profileImg="
				+ profileImg + ", introduce=" + introduce + ", likeCnt=" + likeCnt + ", commentCnt=" + commentCnt + "]";
	}
    
    
}
