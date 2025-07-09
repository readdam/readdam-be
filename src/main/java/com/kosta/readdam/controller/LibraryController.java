package com.kosta.readdam.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.UserSimpleDto;
import com.kosta.readdam.service.LibraryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LibraryController {
	private final LibraryService libraryService;

    @GetMapping("/library/lifeBook/users")
    public List<UserSimpleDto> getUsersFromLifeBook(@RequestParam String isbn) {
        return libraryService.getTop15UsersByIsbnFromLifeLibrary(isbn);
    }
}
