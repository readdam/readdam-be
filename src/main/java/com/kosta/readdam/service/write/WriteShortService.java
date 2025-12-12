package com.kosta.readdam.service.write;

import java.util.List;
import java.util.Map;

import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.User;

public interface WriteShortService {

	WriteShortDto writePostcard(WriteShortDto writeShortDto, User user); // 등록
    Map<String, Object> getWriteShortListByCurrentEvent(int page, int size, String username);
    boolean toggleLike(String username, Integer writeshortId);
    List<WriteShortDto> findLatest(int limit, String username) throws Exception; 
    WriteShortDto updatePostcard(WriteShortDto writeShortDto, User user); // 수정
}
