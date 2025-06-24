package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.dto.WriteDto;

public interface MyWriteService {

	List<WriteDto> getMyWrites(String username) throws Exception;

	List<WriteCommentDto> getMyWriteComments(String username) throws Exception;
	
	WriteDto getWriteDetail(Integer writeId) throws Exception;
}
