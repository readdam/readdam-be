package com.kosta.readdam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.book.BookSearchResultDto;
import com.kosta.readdam.service.BookSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/bookSearch")
    public ResponseEntity<BookSearchResultDto> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "accuracy") String sort,
            @RequestParam(required = false) String target
    ) {
        BookSearchResultDto result = bookSearchService.searchBooks(query, target, sort, page, size);
        return ResponseEntity.ok(result);
    }
}