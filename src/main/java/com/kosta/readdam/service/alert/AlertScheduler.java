package com.kosta.readdam.service.alert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.repository.AlertRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

	private final AlertRepository alertRepository;
	private final NotificationService notificationService;

	/**
	 * 매분 30초마다 실행. scheduledTime 이 현재 시각 이전(<= now)인 모든 알림을 찾아 푸시 발송 후,
	 * scheduledTime 을 null 로 설정해 재발송을 방지합니다.
	 */
	@Scheduled(cron = "30 * * * * *")
	@Transactional
	public void dispatchScheduledAlerts() {
		LocalDateTime now = LocalDateTime.now();
		List<Alert> toSend = alertRepository.findByScheduledTimeLessThanEqual(now);

		for (Alert alert : toSend) {
			try {
				notificationService.sendPush(alert.getReceiver().getUsername(),
						/* 제목이 따로 없다면 content 일부를 사용하거나 서비스 로직에 제목 필드를 추가하세요 */
						"알림", alert.getContent(), Map.of("type", alert.getType(), "linkUrl",
								alert.getLinkUrl() != null ? alert.getLinkUrl() : ""));
				// 발송 완료 표시: scheduledTime 을 null 로 설정
				alert.setScheduledTime(null);
			} catch (Exception e) {
				log.error("예약 알림 발송 중 오류 (alertId={})", alert.getAlertId(), e);
			}
		}
		// @Transactional 이므로 변경된 scheduledTime 값이 커밋 시점에 자동 저장됩니다.
	}
	
	@Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deleteExpiredAlerts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int deleted = alertRepository.deleteByCreatedAtBefore(cutoff);
        log.info("[AlertScheduler] 30일 지난 알림 삭제: {}건", deleted);
    }

}
