package com.kosta.readdam.service.crawling;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BestsellerScheduler {
	
	private final BestsellerService service;
	
    @Scheduled(cron = "*/10 * * * * *")
    public void refreshBestsellers() throws IOException {
    	log.info("테스트 스케줄러 시작: 베스트셀러 업데이트");
        service.updateAll();
        log.info("테스트 스케줄러 완료");
    }

}
