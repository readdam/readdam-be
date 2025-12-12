package com.kosta.readdam.service.admin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.chart.AdminPointStatsDto;
import com.kosta.readdam.dto.chart.ChartDataDto;
import com.kosta.readdam.dto.chart.SummaryDto;
import com.kosta.readdam.entity.enums.PaymentStatus;
import com.kosta.readdam.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService{
	
	
    private final OrderRepository orderRepo;
    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public AdminPointStatsDto getPointStats(
            LocalDate startDate,
            LocalDate endDate,
            String period  // "day" or "month"
    ) throws Exception {
        // — 1) SUMMARY 계산 —
        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime today0 = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime week0  = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                  .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime month0 = now.withDayOfMonth(1)
                                  .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime year0  = now.withDayOfYear(1)
                                  .withHour(0).withMinute(0).withSecond(0);

        SummaryDto summary = new SummaryDto();
        // today
        summary.setTodayAmount(orderRepo.sumPriceByStatusBetween(
            PaymentStatus.APPROVED, today0, now));
        summary.setTodayCount(orderRepo.countByStatusBetween(
            PaymentStatus.APPROVED, today0, now));
        // week
        summary.setWeekAmount(orderRepo.sumPriceByStatusBetween(
            PaymentStatus.APPROVED, week0, now));
        summary.setWeekCount(orderRepo.countByStatusBetween(
            PaymentStatus.APPROVED, week0, now));
        // month
        summary.setMonthAmount(orderRepo.sumPriceByStatusBetween(
            PaymentStatus.APPROVED, month0, now));
        summary.setMonthCount(orderRepo.countByStatusBetween(
            PaymentStatus.APPROVED, month0, now));
        // year
        summary.setYearAmount(orderRepo.sumPriceByStatusBetween(
            PaymentStatus.APPROVED, year0, now));
        summary.setYearCount(orderRepo.countByStatusBetween(
            PaymentStatus.APPROVED, year0, now));

        // — 2) CHART 데이터 조회 (Native Query 사용) —
        String groupExpr = period.equals("month")
            ? "DATE_FORMAT(o.requested_at, '%Y-%m')"
            : "DATE_FORMAT(o.requested_at, '%Y-%m-%d')";
        String sql =
            "SELECT " + groupExpr + "  AS label, " +
            		"  IFNULL(SUM(CASE WHEN o.payment_status <> 'PENDING' THEN o.price ELSE 0 END),0) AS totalAmount, "+
            "       IFNULL(SUM(CASE WHEN o.payment_status = 'CANCELLED' THEN o.price ELSE 0 END),0)   AS refundAmount, " +
            "       IFNULL(COUNT(CASE WHEN o.payment_status = 'APPROVED' THEN 1 END),0)              AS successCount, " +
            "       IFNULL(COUNT(CASE WHEN o.payment_status = 'CANCELLED' THEN 1 END),0)             AS refundCount " +
            "FROM `order` o " +
            "WHERE o.requested_at BETWEEN :start AND :end " +
            "GROUP BY label " +
            "ORDER BY label";

        Query q = em.createNativeQuery(sql)
            .setParameter("start", startDate.atStartOfDay())
            .setParameter("end",   endDate.atTime(23,59,59));

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();

        List<ChartDataDto> chart = rows.stream().map(r -> {
            String    label        = (String) r[0];
            long      totalAmt     = ((BigDecimal) r[1]).longValue();
            long      refundAmt    = ((BigDecimal) r[2]).longValue();
            long      successCnt   = ((BigInteger) r[3]).longValue();
            long      refundCnt    = ((BigInteger) r[4]).longValue();
            long      netAmt       = totalAmt - refundAmt;
            return new ChartDataDto(
                label, totalAmt, refundAmt, netAmt, successCnt, refundCnt
            );
        }).collect(Collectors.toList());

        // — 3) DTO 결합 및 반환 —
        AdminPointStatsDto result = new AdminPointStatsDto();
        result.setSummary(summary);
        result.setChart(chart);
        return result;
    }
}