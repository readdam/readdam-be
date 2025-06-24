package com.kosta.readdam.service.write;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	private final WriteCommentRepository writeCommentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private final WriteRepository writeRepository;
	
	@Override
	public List<WriteCommentDto> findByWriteId(Integer writeId) {
	    return writeCommentRepository.findByWrite_WriteId(writeId).stream()
	            .map(WriteComment::toDto)
	            .collect(Collectors.toList());
	    }


	@Override
	public void save(WriteCommentDto dto) {
	    User user = userRepository.findByUsername(dto.getUsername())
	        .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

	    Write write = writeRepository.findById(dto.getWriteId())
	        .orElseThrow(() -> new IllegalArgumentException("글 없음"));

	    writeCommentRepository.save(dto.toEntity(write, user));
	}
}
