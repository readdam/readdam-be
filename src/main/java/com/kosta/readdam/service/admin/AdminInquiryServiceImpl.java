package com.kosta.readdam.service.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.InquiryDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.Inquiry;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.InquiryStatus;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.InquiryRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.spec.InquirySpecification;
import com.kosta.readdam.service.alert.NotificationService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInquiryServiceImpl implements AdminInquiryService {

    private final InquiryRepository inquiryRepository;
	private final UserRepository userRepository;
	private final AlertRepository alertRepository;
	private final NotificationService notificationService;

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
        
        User receiver = inq.getUser();                     // 문의 작성자
        User system   = userRepository.findByUsername("system")
                            .orElseThrow();               // 시스템 계정

        String type    = "inquiry";
        String title   = String.format("문의 \"%s\"에 답변이 등록되었습니다.", inq.getTitle());
        String content = answerText;
        String path    = "/myInquiry";

        // Alert 엔티티에 linkUrl 포함
        Alert alert = Alert.builder()
            .sender(system)
            .receiver(receiver)
            .type(type)
            .title(title)
            .content(content)
            .linkUrl(path)
            .build();
        alertRepository.save(alert);

        // FCM 푸시
        Map<String,String> data = new HashMap<>();
        data.put("type",    type);
        data.put("linkUrl", path);
        notificationService.sendPush(receiver.getUsername(), title, content, data);
    }
    
    
}
