package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Notice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDto {

    private Integer noticeId;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private Boolean topFix;

    public Notice toEntity() {
        return Notice.builder()
                .noticeId(noticeId)
                .title(title)
                .content(content)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .topFix(topFix != null ? topFix : false)
                .build();
    }
}
