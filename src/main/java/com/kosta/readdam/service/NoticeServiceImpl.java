package com.kosta.readdam.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.entity.Notice;
import com.kosta.readdam.repository.NoticeRepository;
import com.kosta.readdam.util.PageInfo;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	
	private final NoticeRepository noticeRepository;

	@Override
	public Map<String, Object> getNoticeList(int page, int size, String keyword) throws Exception {
		Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Order.desc("topFix"), Sort.Order.desc("regDate"))
        );

        List<NoticeDto> fixedNotices;
        List<NoticeDto> normalNotices;
        Page<Notice> noticePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색 시 → 고정/일반 모두 포함
            noticePage = noticeRepository
                    .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByTopFixDescRegDateDesc(
                            keyword,
                            keyword,
                            pageable
                    );
            if (noticePage.isEmpty()) {
                throw new Exception("검색된 공지사항이 없습니다.");
            }

            fixedNotices = noticePage.getContent().stream()
                    .filter(Notice::getTopFix)
                    .map(NoticeDto::fromEntity)
                    .collect(Collectors.toList());

            normalNotices = noticePage.getContent().stream()
                    .filter(notice -> !notice.getTopFix())
                    .map(NoticeDto::fromEntity)
                    .collect(Collectors.toList());

        } else {
            // 검색 없으면 기존 로직 (고정/일반 분리)
            List<Notice> fixedList = noticeRepository
                    .findByTopFixTrueOrderByRegDateDesc();

            fixedNotices = fixedList.stream()
                    .map(NoticeDto::fromEntity)
                    .collect(Collectors.toList());

            noticePage = noticeRepository
                    .findByTopFixFalseOrderByRegDateDesc(pageable);
            
            if (noticePage.isEmpty() && fixedNotices.isEmpty()) {
                throw new Exception("등록된 공지사항이 없습니다.");
            }

            normalNotices = noticePage.getContent().stream()
                    .map(NoticeDto::fromEntity)
                    .collect(Collectors.toList());
        }

        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurPage(noticePage.getNumber() + 1);
        pageInfo.setAllPage(noticePage.getTotalPages());
        pageInfo.setStartPage(1);
        pageInfo.setEndPage(noticePage.getTotalPages());

        Map<String, Object> result = new HashMap<>();
        result.put("fixedNotices", fixedNotices);
        result.put("normalNotices", normalNotices);
        result.put("pageInfo", pageInfo);

        return result;
    }

}
