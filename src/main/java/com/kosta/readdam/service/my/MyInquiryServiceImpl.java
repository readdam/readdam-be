package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.InquiryStatus;
import com.kosta.readdam.repository.InquiryRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.util.PageInfo2;

@Service
public class MyInquiryServiceImpl implements MyInquiryService{
	
	@Autowired
	private InquiryRepository inquiryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
    public PagedResponse<InquiryDto> getMyInquiryList(String username, int page, int size) throws Exception {
        // 0-based page index, regDate 내림차순
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Inquiry> inquiryPage = inquiryRepository.findByUser_Username(username, pageable);

        List<InquiryDto> dtoList = inquiryPage.getContent()
                                             .stream()
                                             .map(Inquiry::toDto)
                                             .collect(Collectors.toList());

        // PageInfo2 생성
        PageInfo2 pageInfo = PageInfo2.from(inquiryPage);

        // 래핑해서 반환
        return new PagedResponse<>(dtoList, pageInfo);
    }
	 
	 @Override
	 public InquiryDto writeInquiry(String username, InquiryDto dto) throws Exception {
	     User user = userRepository.findById(username)
	             .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
	     
	     Inquiry inquiry = dto.toEntity(user);
	     Inquiry saved = inquiryRepository.save(inquiry);

	     return saved.toDto();
	 }
	 
	 @Override
	 @Transactional
	 public InquiryDto updateInquiry(String username, InquiryDto dto) throws Exception {
	     Inquiry inquiry = inquiryRepository.findById(dto.getInquiryId())
	         .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다."));

	     if (!inquiry.getUser().getUsername().equals(username)) {
	         throw new IllegalAccessException("권한이 없습니다.");
	     }

	     if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
	         throw new IllegalStateException("답변 완료된 문의는 수정할 수 없습니다.");
	     }

	     inquiry.setTitle(dto.getTitle());
	     inquiry.setContent(dto.getContent());
	     inquiry.setReason(dto.getReason());

	     Inquiry updated = inquiryRepository.save(inquiry);
	     return InquiryDto.builder()
	         .inquiryId(updated.getInquiryId())
	         .title(updated.getTitle())
	         .content(updated.getContent())
	         .reason(updated.getReason())
	         .status(updated.getStatus())
	         .answer(updated.getAnswer())
	         .regDate(updated.getRegDate())
	         .username(updated.getUser().getUsername())
	         .build();
	 }

	 @Override
	 @Transactional
	 public void deleteInquiry(String username, Integer inquiryId) throws Exception {
	     Inquiry inquiry = inquiryRepository.findById(inquiryId)
	         .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다."));

	     if (!inquiry.getUser().getUsername().equals(username)) {
	         throw new IllegalAccessException("권한이 없습니다.");
	     }

	     if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
	         throw new IllegalStateException("답변 완료된 문의는 삭제할 수 없습니다.");
	     }

	     inquiryRepository.delete(inquiry);
	 }


	
	

}
