package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.entity.enums.InquiryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inquiry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", nullable = false, updatable = false)
    private Integer inquiryId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;
    
    @Column(name = "answer_date")
    private LocalDateTime answerDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(nullable = false)
    private String reason;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;

    public InquiryDto toDto() {
        return InquiryDto.builder()
                .inquiryId(inquiryId)
                .title(title)
                .content(content)
                .answer(answer)
                .regDate(regDate)
                .answerDate(answerDate)
                .status(status)
                .username(user.getUsername())
                .reason(reason)
                .isHide(isHide)
                .build();
    }
}
