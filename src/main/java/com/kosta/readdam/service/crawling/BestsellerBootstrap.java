package com.kosta.readdam.service.crawling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.kosta.readdam.entity.enums.BookListCategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BestsellerBootstrap implements CommandLineRunner {

    private final BestsellerService bestsellerService;
    private final KyoboCrawlerService crawler;

    @Override
    public void run(String... args) {
    	if (crawler.isEmpty(BookListCategory.WEEKLY)
                || crawler.isEmpty(BookListCategory.MONTHLY)) {

                log.info("▶ 초기 데이터 비어 있음 → 베스트셀러 적재 시작");
                bestsellerService.updateAll();
                log.info("◀ 초기 베스트셀러 적재 완료");
            } else {
                log.info("▶ 초기 데이터 이미 존재 → 적재 생략");
            }
    }
}