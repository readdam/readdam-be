package com.kosta.readdam.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.AllSearchDto;
import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.SearchResultDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.service.BookSearchService;
import com.kosta.readdam.service.klass.ClassService;
import com.kosta.readdam.service.otherPlace.OtherPlaceService;
import com.kosta.readdam.service.place.PlaceService;
import com.kosta.readdam.service.write.WriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AllSearchController {

    private final PlaceService placeService;
    private final OtherPlaceService otherPlaceService;
    private final ClassService classService;
    private final WriteService writeService;
    private final BookSearchService bookSearchService;
    
    @GetMapping("/allSearch")
    public ResponseEntity<?> allSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "latest") String sort
    	    ) {

        	// 각 서비스에서 최신기준 limit=4씩 가져오기
            try {
                SearchResultDto<PlaceDto> placeResult = placeService.searchForAll(keyword, sort, 4);
                SearchResultDto<PlaceDto> otherPlaceResult  = otherPlaceService.searchForAll(keyword, sort, 4);

                // Place + OtherPlace 합치기
                List<PlaceDto> combinedPlaces = Stream.concat(
                        placeResult.getContent().stream(),
                        otherPlaceResult.getContent().stream()
                )
                .limit(4)
                .collect(Collectors.toList());

                int totalCount = placeResult.getTotalCount() + otherPlaceResult.getTotalCount();

                SearchResultDto<PlaceDto> combinedPlaceResult =
                        new SearchResultDto<>(combinedPlaces, totalCount);
            SearchResultDto<ClassDto> classResult = classService.searchForAll(keyword, sort, 4);
            SearchResultDto<WriteDto> writeResult = writeService.searchForAll(keyword, sort, 4);
            // 책만 정확도순
            SearchResultDto<BookDto> bookResult = bookSearchService.searchForAll(keyword, "accuracy", 4);
        
	        AllSearchDto dto = new AllSearchDto(
	        		combinedPlaceResult,
	                classResult,
	                writeResult,
	                bookResult
	        );
	        
        return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
