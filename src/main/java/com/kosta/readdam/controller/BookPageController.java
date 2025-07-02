package com.kosta.readdam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kosta.readdam.dto.BookListDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.enums.BookListCategory;
import com.kosta.readdam.service.BookListService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookPageController {

    private final BookListService bookListService;

    @GetMapping("/bestsellers")
    public ResponseEntity<PagedResponse<BookListDto>> getBestsellers(
            @RequestParam BookListCategory period,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<BookListDto> resp =
            bookListService.getBestsellersByCategory(period, page, size);
        return ResponseEntity.ok(resp);
    }
}
