package com.kosta.readdam.service.write;

import java.util.List;

import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.entity.User;

public interface WriteCommentService {
    List<WriteCommentDto> findByWriteId(Integer writeId) throws Exception;

	void save(WriteCommentDto dto) throws Exception;
	
}
