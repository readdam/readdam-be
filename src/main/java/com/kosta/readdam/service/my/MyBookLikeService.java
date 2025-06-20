package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.BookLikeDto;

public interface MyBookLikeService {

	List<BookLikeDto> getLikedBooksByUsername(String username) throws Exception;

}
