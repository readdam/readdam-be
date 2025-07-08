package com.kosta.readdam.service.my;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;

public interface MyInquiryService {
	
	PagedResponse<InquiryDto> getMyInquiryList(String username, int page, int size) throws Exception;

	InquiryDto writeInquiry(String username, InquiryDto dto) throws Exception;

	void deleteInquiry(String username, Integer inquiryId) throws Exception;

	InquiryDto updateInquiry(String username, InquiryDto dto) throws Exception;

}
