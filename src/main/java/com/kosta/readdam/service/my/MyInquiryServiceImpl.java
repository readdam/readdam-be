package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.InquiryStatus;
import com.kosta.readdam.repository.InquiryRepository;
import com.kosta.readdam.repository.UserRepository;

@Service
public class MyInquiryServiceImpl implements MyInquiryService{
	
	@Autowired
	private InquiryRepository inquiryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	 @Override
	    public List<InquiryDto> getMyInquiryList(String username) throws Exception {
	        List<Inquiry> list = inquiryRepository.findByUser_UsernameOrderByRegDateDesc(username);
	        System.out.println("조회된 문의 수: " + list.size());
	        return list.stream().map(Inquiry::toDto).collect(Collectors.toList());
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
