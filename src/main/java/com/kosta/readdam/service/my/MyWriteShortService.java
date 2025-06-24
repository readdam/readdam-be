package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.WriteShortDto;

public interface MyWriteShortService {

	List<WriteShortDto> getMyShorts(String username) throws Exception;

	long toggleLike(String username, Integer writeshortId) throws Exception;

}
