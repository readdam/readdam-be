package com.kosta.readdam.service.crawling;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.BookList;
import com.kosta.readdam.entity.enums.BookListCategory;
import com.kosta.readdam.repository.BookListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BestsellerService {

    private final KyoboCrawlerService crawler;
    private final BookListRepository repository;

    /**
     * 1) 기존 카테고리 데이터 삭제
     * 2) 크롤링된 데이터를 저장
     */
    @Transactional
    public void updateCategory(BookListCategory category) {
        log.info("[서비스] category={} 시작", category);

        repository.deleteByCategory(category);

        List<BookList> list = crawler.fetchBookList(category);
        log.info("[서비스] category={} 저장할 리스트: {}", category, list.size());

        // ⚠️ saveAllAndFlush 로 바꾸면 트랜잭션 안에서 곧바로 INSERT-SQL 실행
        repository.saveAllAndFlush(list);   // <— ★
    }


    /** WEEKLY, MONTHLY, YEARLY 순으로 모두 갱신 */
    public void updateAll() {
        updateCategory(BookListCategory.WEEKLY);
        updateCategory(BookListCategory.MONTHLY);
        updateCategory(BookListCategory.DAILY);
    }

}
