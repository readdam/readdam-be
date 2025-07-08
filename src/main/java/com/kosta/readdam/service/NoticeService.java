package com.kosta.readdam.service;

import java.util.Map;

public interface NoticeService {
	Map<String, Object> getNoticeList(int page, int size, String keyword) throws Exception;
}
