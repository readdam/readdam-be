package com.kosta.readdam.service;

import com.kosta.readdam.dto.BookListDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.enums.BookListCategory;

public interface BookListService {
	
	 PagedResponse<BookListDto> getBestsellersByCategory(
		        BookListCategory category, int page, int size
		    );

}
