package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.InquiryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDto {

    private Integer inquiryId;
    private String title;
    private String content;
    private String answer;
    private LocalDateTime regDate;
    private LocalDateTime answerDate;
    private InquiryStatus status;
    private String username;
    private String reason;
    private Boolean isHide;

    public Inquiry toEntity(User user) {
        return Inquiry.builder()
                .inquiryId(inquiryId)
                .title(title)
                .content(content)
                .answer(answer)
                .regDate(regDate != null ? regDate : LocalDateTime.now())
                .answerDate(answerDate)
                .status(status != null ? status : InquiryStatus.UNANSWERED)
                .user(user)
                .reason(reason)
                .isHide(isHide != null ? isHide : false)
                .build();
    }
}
