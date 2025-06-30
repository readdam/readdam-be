package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Report;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

    private Integer reportId;
    private String reporterUsername;
    private String reportedUsername;
    private String reason;
    private String category;
    private String categoryId;
    private LocalDateTime reportedAt;
    private LocalDateTime processedAt;
    private ReportStatus status;

    public Report toEntity(User reporter, User reported) {
        return Report.builder()
                .reportId(reportId)
                .reporter(reporter)
                .reported(reported)
                .reason(reason)
                .category(category)
                .categoryId(categoryId)
                .reportedAt(reportedAt != null ? reportedAt : LocalDateTime.now())
                .status(status != null ? status : ReportStatus.PENDING) // 기본값 설정 가능
                .build();
    }
}
