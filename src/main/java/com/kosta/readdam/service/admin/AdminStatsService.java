package com.kosta.readdam.service.admin;

import java.time.LocalDate;

import com.kosta.readdam.dto.chart.AdminPointStatsDto;

public interface AdminStatsService {

	AdminPointStatsDto getPointStats(LocalDate startDate, LocalDate endDate, String period) throws Exception;

}
