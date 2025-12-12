package com.kosta.readdam.repository;

import java.util.List;

import com.kosta.readdam.dto.UserSimpleDto;

public interface LibraryBookRepositoryCustom {
	List<UserSimpleDto> findTop15UsersByIsbnAndLibraryName(String isbn, String libraryName);
}
