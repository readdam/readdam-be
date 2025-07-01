package com.kosta.readdam.service.crawling;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.BookList;
import com.kosta.readdam.entity.enums.BookListCategory;
import com.kosta.readdam.repository.BookListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BestsellerService {
	
	private final SeleniumCrawlerService crawler;
    private final BookListRepository repo;

    @Transactional
    public void updateCategory(BookListCategory category) throws IOException {
        repo.deleteByCategory(category);

        List<BookList> list = crawler.fetchBookList(category);
        repo.saveAll(list);
    }

    public void updateAll() throws IOException {
        for (BookListCategory c : BookListCategory.values()) {
            updateCategory(c);
        }
    }

}
