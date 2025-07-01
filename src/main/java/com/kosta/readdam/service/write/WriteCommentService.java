package com.kosta.readdam.service.write;

import java.util.List;

import com.kosta.readdam.dto.WriteCommentDto;

public interface WriteCommentService {
    List<WriteCommentDto> findByWriteId(Integer writeId) throws Exception;

	void save(WriteCommentDto dto) throws Exception;
	
	//이미 채택된 댓글이 있는지 확인
	boolean existsByWrite_WriteIdAndAdoptedTrue(Integer writeId) throws Exception;
	//실제 채택 처리
	void adoptComment(Integer writeCommentId) throws Exception;
}
