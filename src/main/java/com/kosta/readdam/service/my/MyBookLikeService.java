package com.kosta.readdam.service.my;

import org.springframework.data.domain.Page;

import com.kosta.readdam.dto.BookDto;

public interface MyBookLikeService {

	Page<BookDto> getLikedBooksByUsername(String username, int page, int size);
	
}
