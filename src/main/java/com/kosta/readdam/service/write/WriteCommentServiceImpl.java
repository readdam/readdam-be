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
	    
	    // [추가] 내 글에는 댓글 작성 금지
	    if (write.getUser().getUsername().equals(dto.getUsername())) {
	        throw new IllegalStateException("본인의 글에는 댓글을 작성할 수 없습니다.");
	    }

	    // [추가] 이미 댓글을 썼는지 확인
	    boolean exists = writeCommentRepository
	            .existsByWrite_WriteIdAndUser_Username(dto.getWriteId(), dto.getUsername());
	    if (exists) {
	        throw new IllegalStateException("이미 이 글에 댓글을 작성하였습니다.");
	    }

	    writeCommentRepository.save(dto.toEntity(write, user));
	    
	    // 댓글 수 +1 처리
	    writeRepository.updateCommentCnt(write.getWriteId(), 1);
	}

	@Override
	public boolean existsByWrite_WriteIdAndAdoptedTrue(Integer writeId) throws Exception {
		return writeCommentRepository.existsByWrite_WriteIdAndAdoptedTrue(writeId);
	}

	@Transactional
	@Override
	public void adoptComment(Integer writeCommentId) throws Exception {
	    WriteComment comment = writeCommentRepository.findById(writeCommentId)
	            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

	    Integer writeId = comment.getWrite().getWriteId();

	    boolean alreadyAdopted = writeCommentRepository.existsByWrite_WriteIdAndAdoptedTrue(writeId);
	    if (alreadyAdopted) {
	        throw new IllegalStateException("이미 채택된 댓글이 존재합니다.");
	    }

	    comment.setAdopted(true);
	    writeCommentRepository.save(comment);
		
	}
}
