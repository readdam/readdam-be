package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.LibraryBookDto;
import com.kosta.readdam.dto.LibraryDto;

public interface MyLibraryService {

	List<LibraryDto> getMyLibraryList(String username) throws Exception;

	LibraryDto updateLibrary(LibraryDto dto) throws Exception;

	void deleteLibrary(Integer libraryId) throws Exception;

	void addLibrary(String username, LibraryDto dto);

	 List<LibraryDto> toggleShowAll(String username, Integer isShow) throws Exception;
	 
	 List<LibraryDto> getMyLibraries(String username);
	 
	 void addBookToLibrary(String username, Integer libraryId, LibraryBookDto bookDto);
}
