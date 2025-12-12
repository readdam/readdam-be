package com.kosta.readdam.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.service.BookClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookClass")
@RequiredArgsConstructor
public class BookClassController {
	private final BookClassService bookClassService;

    /**
     * 책 제목과 저자를 기준으로 해당 책이 사용된 클래스(모임) 목록 조회
     */
    @GetMapping("/byBook")
    public ResponseEntity<List<ClassDto>> getClassesByBook(
            @RequestParam String title,
            @RequestParam String author
    ) {
        List<ClassDto> classes = bookClassService.findClassesByBook(title, author);
        return ResponseEntity.ok(classes);
    }
}
