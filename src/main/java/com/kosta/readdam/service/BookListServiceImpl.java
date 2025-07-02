package com.kosta.readdam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookListDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.enums.BookListCategory;
import com.kosta.readdam.repository.BookListDslRepository;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookListServiceImpl implements BookListService {
    private final BookListDslRepository repo;

    @Override
    public PagedResponse<BookListDto> getBestsellersByCategory(
            BookListCategory category, int page, int size) {

        Page<BookListDto> pg = repo.findBookListPageWithStats(
            category, PageRequest.of(page - 1, size)
        );

        PageInfo2 info = PageInfo2.from(pg);
        return new PagedResponse<>(pg.getContent(), info);
    }
}