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
@Builder
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
}
