package com.kosta.readdam.service.admin;

import java.util.List;

import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.entity.Notice;

public interface AdminNoticeService {

	Notice createNotice(NoticeDto dto) throws Exception;
	List<NoticeDto> getAllNotices() throws Exception;
	Notice getNoticeById(Integer noticeId) throws Exception;
	void deleteNoticeById(Integer noticeId) throws Exception;
	Notice updateNotice(Integer noticeId, NoticeDto dto) throws Exception;

}
