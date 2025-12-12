package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.BookReviewDto;

public interface MyBookReviewService {

	List<BookReviewDto> getReviewsByUsername(String username) throws Exception;


}
