package com.kosta.readdam.controller.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.chart.AdminPointStatsDto;
import com.kosta.readdam.service.admin.AdminStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;
    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/point-stats")
    public ResponseEntity<?> getPointStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam String period  // "day" or "month"
    ) {
        try {
            LocalDate s = LocalDate.parse(start, DF);
            LocalDate e = LocalDate.parse(end,   DF);
            AdminPointStatsDto dto = adminStatsService.getPointStats(s, e, period);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            log.error("포인트 통계 조회 중 오류", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("포인트 통계 조회 실패: " + ex.getMessage());
        }
    }

}
