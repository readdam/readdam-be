package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.InquiryDto;

public interface MyInquiryService {
	
	List<InquiryDto> getMyInquiryList(String username) throws Exception;

	InquiryDto writeInquiry(String username, InquiryDto dto) throws Exception;

	void deleteInquiry(String username, Integer inquiryId) throws Exception;

	InquiryDto updateInquiry(String username, InquiryDto dto) throws Exception;

}
