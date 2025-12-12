package com.kosta.readdam.entity;

import com.kosta.readdam.dto.NoticeDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", nullable = false, updatable = false)
    private Integer noticeId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "top_fix", nullable = false)
    private Boolean topFix;

    public NoticeDto toDto() {
        return NoticeDto.builder()
                .noticeId(noticeId)
                .title(title)
                .content(content)
                .regDate(regDate)
                .topFix(topFix)
                .build();
    }
}
