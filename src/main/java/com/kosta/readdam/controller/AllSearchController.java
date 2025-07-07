package com.kosta.readdam.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.AllSearchDto;
import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.service.BookSearchService;
import com.kosta.readdam.service.klass.ClassService;
import com.kosta.readdam.service.place.PlaceService;
import com.kosta.readdam.service.write.WriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AllSearchController {

    private final PlaceService placeService;
    private final ClassService classService;
    private final WriteService writeService;
    private final BookSearchService bookSearchService;
    
    @GetMapping("/allSearch")
    public ResponseEntity<AllSearchDto> allSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "latest") String sort
    	    ) {
    	
        // 각 서비스에서 limit=4씩 가져오기
        List<PlaceDto> places = placeService.searchForAll(keyword, sort, 4);
        List<ClassDto> classes = classService.searchForAll(keyword, sort, 4);
        List<WriteDto> writes = writeService.searchForAll(keyword, sort, 4);
        List<BookDto> books = bookSearchService.searchForAll(keyword, sort, 4);

        return ResponseEntity.ok(new AllSearchDto(places, classes, writes, books));
    }
}
