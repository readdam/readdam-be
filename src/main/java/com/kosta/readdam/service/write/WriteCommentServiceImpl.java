package com.kosta.readdam.service.write;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.entity.WriteComment;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class WriteCommentServiceImpl implements WriteCommentService {

	private final WriteCommentRepository writeCommentRepository;
	private final UserRepository userRepository;
	private final WriteRepository writeRepository;
	
	@Override
	public List<WriteCommentDto> findByWriteId(Integer writeId) throws Exception {
	    return writeCommentRepository.findByWrite_WriteId(writeId).stream()
	            .map(WriteComment::toDto)
	            .collect(Collectors.toList());
	    }

	@Transactional
	@Override
	public void save(WriteCommentDto dto) throws Exception{
	    User user = userRepository.findByUsername(dto.getUsername())
	        .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

	    Write write = writeRepository.findById(dto.getWriteId())
	        .orElseThrow(() -> new IllegalArgumentException("글 없음"));

	    writeCommentRepository.save(dto.toEntity(write, user));
	    
	    // 댓글 수 +1 처리
	    writeRepository.updateCommentCnt(write.getWriteId(), 1);
	}
}
