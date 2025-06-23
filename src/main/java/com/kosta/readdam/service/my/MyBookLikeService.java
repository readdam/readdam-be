package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.BookDto;

public interface MyBookLikeService {

	List<BookDto> getLikedBooksByUsername(String username) throws Exception;
	
}
