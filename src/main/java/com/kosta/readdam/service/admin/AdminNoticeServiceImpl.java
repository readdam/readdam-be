package com.kosta.readdam.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.entity.Notice;
import com.kosta.readdam.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {	
	
	private final NoticeRepository noticeRepository;
	

	@Override
	@Transactional
	public Notice createNotice(NoticeDto dto) throws Exception {
			Notice notice = new Notice();
			notice.setTitle(dto.getTitle());
			notice.setContent(dto.getContent());
			notice.setTopFix(dto.getTopFix());
			notice.setRegDate(LocalDateTime.now());
			
			return noticeRepository.save(notice);
	}

	@Override
	public List<NoticeDto> getAllNotices() throws Exception {
		List<Notice> noticeList = noticeRepository.findAllByOrderByTopFixDescRegDateDesc();
		return noticeList.stream()
				.map(NoticeDto::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public Notice getNoticeById(Integer noticeId) throws Exception {
		return noticeRepository.findById(noticeId)
				.orElseThrow(()-> new Exception("NoticeId "+ noticeId + "번 공지사항 없음"));
	}

}
