package com.kosta.readdam.service.write;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.Point;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.entity.WriteComment;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.PointRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteRepository;
import com.kosta.readdam.service.alert.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WriteCommentServiceImpl implements WriteCommentService {

	private final WriteCommentRepository writeCommentRepository;
	private final UserRepository userRepository;
	private final WriteRepository writeRepository;
	private final PointRepository pointRepository;
	private final AlertRepository alertRepository;
	private final NotificationService notificationService;
	
	private static final int ADOPT_REWARD = 100;
	
	@Override
	public List<WriteCommentDto> findByWriteId(Integer writeId) throws Exception {
	    return writeCommentRepository.findByWrite_WriteIdAndIsHideFalse(writeId).stream()
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

	 // 숨김 안된 댓글이 이미 존재하면 작성 불가
	    long visibleCommentCnt = writeCommentRepository.countByWrite_WriteIdAndUser_UsernameAndIsHideFalse(
	    	    dto.getWriteId(), dto.getUsername()
	    		);
	    if (visibleCommentCnt > 0) {
	        throw new IllegalStateException("이미 이 글에 댓글을 작성하였습니다.");
	    }

	    writeCommentRepository.save(dto.toEntity(write, user));
	    
	 // 숨김되지 않은 댓글 수로 갱신
	    int commentCount = Math.toIntExact(
	            writeCommentRepository.countByWrite_WriteIdAndIsHideFalse(write.getWriteId())
	    );
	    writeRepository.updateCommentCnt(write.getWriteId(), commentCount);
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

//	    Integer writeId = comment.getWrite().getWriteId();
//
//	    boolean alreadyAdopted = writeCommentRepository.existsByWrite_WriteIdAndAdoptedTrue(writeId);
//	    if (alreadyAdopted) {
//	        throw new IllegalStateException("이미 채택된 댓글이 존재합니다.");
//	    }
	    
	    Write write = comment.getWrite();
        User postAuthor = write.getUser();           
        User commentAuthor = comment.getUser();
        
        if (postAuthor.getTotalPoint() == null || postAuthor.getTotalPoint() < ADOPT_REWARD) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

	    comment.setAdopted(true);
	    writeCommentRepository.save(comment);
	    
	    postAuthor.setTotalPoint(postAuthor.getTotalPoint() - ADOPT_REWARD);
        // 기록 생성
        Point deduction = Point.builder()
            .user(postAuthor)
            .point(-ADOPT_REWARD)
            .reason("댓글 채택 보상 지급")
            .build();
        pointRepository.save(deduction);

        // 3-2) 댓글 작성자 포인트 지급
        commentAuthor.setTotalPoint(
            (commentAuthor.getTotalPoint() != null ? commentAuthor.getTotalPoint() : 0) + ADOPT_REWARD
        );
        Point reward = Point.builder()
            .user(commentAuthor)
            .point(ADOPT_REWARD)
            .reason("채택된 첨삭 보상")
            .build();
        pointRepository.save(reward);

        // 4) 변경된 사용자 정보 저장
        userRepository.save(postAuthor);
        userRepository.save(commentAuthor);
        String linkUrl = "writeDetail/" + write.getWriteId();
        
        User system = userRepository.findByUsername("system")
                .orElseThrow(() -> new IllegalStateException("시스템 사용자(system)가 없습니다."));

            // Alert 엔티티 생성
            String title   = "첨삭이 채택되었습니다!";
            String content = String.format(
                "[%s] 게시글의 첨삭이 채택되었습니다.", write.getTitle()
            );

            Alert alert = Alert.builder()
                .sender(system)                  // 발신자: system 또는 postAuthor
                .receiver(commentAuthor)         // 수신자: 첨삭 작성자
                .title(title)
                .content(content)
                .type("adopt")
                .linkUrl(linkUrl)
                .build();
            alertRepository.save(alert);

            // FCM 푸시
            Map<String, String> data = new HashMap<>();
            data.put("type", "adopt");
            data.put("link_url", linkUrl);
            notificationService.sendPush(
                commentAuthor.getUsername(),
                title,
                content,
                data
            );
		
	}

	@Override
	public void updateComment(WriteCommentDto dto, PrincipalDetails principal) throws Exception {
		
	    WriteComment comment = writeCommentRepository.findById(dto.getWriteCommentId())
	            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

	    // 본인 작성 댓글인지 체크
	    if (!comment.getUser().getUsername().equals(principal.getUsername())) {
	        throw new IllegalArgumentException("본인이 작성한 댓글만 수정할 수 있습니다.");
	    }

	    comment.setContent(dto.getContent());
	    comment.setIsSecret(dto.getIsSecret() != null ? dto.getIsSecret() : false);
	    
	    writeCommentRepository.save(comment);
	}

	@Override
	public void hideComment(Integer writeCommentId, PrincipalDetails principal) throws Exception {
		
		WriteComment comment = writeCommentRepository.findById(writeCommentId)
	            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

	    // 채택된 댓글은 숨김 불가
	    if (comment.getAdopted() != null && comment.getAdopted()) {
	        throw new IllegalStateException("채택된 댓글은 삭제할 수 없습니다.");
	    }

	    // 본인 댓글인지 확인
	    if (!comment.getUser().getUsername().equals(principal.getUsername())) {
	        throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
	    }

	    if (Boolean.TRUE.equals(comment.getIsHide())) {
	        // 이미 숨김 처리된 댓글 → 아무 것도 안 하고 끝낼 수도 있음
	        log.info("이미 숨김 처리된 댓글입니다. writeCommentId={}", writeCommentId);
	        return;
	    }
	    
	    comment.setIsHide(true);
	    writeCommentRepository.save(comment);

	    // 댓글 수 -1
	    Integer writeId = comment.getWrite().getWriteId();
	    writeRepository.updateCommentCnt(writeId, -1);
	}
}
