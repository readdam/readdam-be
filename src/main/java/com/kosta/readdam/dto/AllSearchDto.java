package com.kosta.readdam.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AllSearchDto {
    private SearchResultDto<PlaceDto> places;
    private SearchResultDto<ClassDto> classes;
    private SearchResultDto<WriteDto> writes;
    private SearchResultDto<BookDto> books;

}
