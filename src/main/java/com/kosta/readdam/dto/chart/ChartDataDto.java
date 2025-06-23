package com.kosta.readdam.dto.chart;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDto {
    private String label;
    private long totalAmount;
    private long refundAmount;
    private long netAmount;
    private long successCount;
    private long refundCount;
}