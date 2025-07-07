package com.kosta.readdam.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AllSearchDto {
    private List<PlaceDto> places;
    private List<ClassDto> classes;
    private List<WriteDto> writes;
    private List<BookDto> books;

}
