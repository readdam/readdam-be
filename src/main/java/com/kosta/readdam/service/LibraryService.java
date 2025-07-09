package com.kosta.readdam.service;

import java.util.List;

import com.kosta.readdam.dto.UserSimpleDto;

public interface LibraryService {

	List<UserSimpleDto> getTop15UsersByIsbnFromLifeLibrary(String isbn);

}