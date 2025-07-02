package com.kosta.readdam.service.crawling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BestsellerScheduler {

    private final BestsellerService service;

    /** 매일 오전 3시에 베스트셀러 업데이트 */
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshBestsellers() {
        log.info("스케줄러 시작: 베스트셀러 업데이트");
        service.updateAll();
        log.info("스케줄러 완료");
    }
}
