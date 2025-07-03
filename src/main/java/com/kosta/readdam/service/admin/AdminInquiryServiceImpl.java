package com.kosta.readdam.service.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.enums.InquiryStatus;
import com.kosta.readdam.repository.InquiryRepository;
import com.kosta.readdam.repository.spec.InquirySpecification;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInquiryServiceImpl implements AdminInquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InquiryDto> getInquiries(
            String filterType,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            int page,
            int size
    ) {
        Specification<Inquiry> spec = Specification.where(null);

        // 검색어
        spec = spec.and(InquirySpecification.hasKeyword(filterType, keyword));
        // 날짜(등록일 기준)
        spec = spec.and(InquirySpecification.betweenDates("등록일", startDate, endDate));
        // 상태
        spec = spec.and(InquirySpecification.hasStatus(status));

        PageRequest pageReq = PageRequest.of(page, size, Sort.by("regDate").descending());
        var inquiryPage = inquiryRepository.findAll(spec, pageReq);

        List<InquiryDto> dtos = inquiryPage.stream()
                .map(Inquiry::toDto)
                .collect(Collectors.toList());

        var pageInfo = PageInfo2.from(inquiryPage);
        return new PagedResponse<>(dtos, pageInfo);
    }

    @Override
    @Transactional
    public void answerInquiry(Integer inquiryId, String answerText) {
        Inquiry inq = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의 ID: " + inquiryId));
        inq.setAnswer(answerText);
        inq.setStatus(InquiryStatus.ANSWERED);
        inq.setAnswerDate(LocalDateTime.now());
        inquiryRepository.save(inq);
    }
}
