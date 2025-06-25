package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.WriteShortDto;

public interface MyWriteShortService {

	List<WriteShortDto> getMyWriteShorts(String username)throws Exception;

    WriteShortDto toggleLike(Integer writeshortId, String username)throws Exception;
}
