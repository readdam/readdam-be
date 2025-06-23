package com.kosta.readdam.dto.chart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SummaryDto {
    private long todayAmount;
    private long todayCount;
    private long weekAmount;
    private long weekCount;
    private long monthAmount;
    private long monthCount;
    private long yearAmount;
    private long yearCount;
}