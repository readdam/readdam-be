package com.kosta.readdam.service.admin;

import java.time.LocalDate;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;

public interface AdminInquiryService {

	PagedResponse<InquiryDto> getInquiries(String filterType, String keyword, LocalDate startDate, LocalDate endDate,
			String status, int page, int size);

	void answerInquiry(Integer inquiryId, String answerText);
}
