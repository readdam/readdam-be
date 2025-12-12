package com.kosta.readdam.dto.chart;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminPointStatsDto {
	private SummaryDto summary;
	private List<ChartDataDto> chart;
}