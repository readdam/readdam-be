package com.kosta.readdam.controller.write;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.repository.WriteLikeRepository;
import com.kosta.readdam.service.write.WriteCommentService;
import com.kosta.readdam.service.write.WriteService;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WriteController {

	private final WriteService writeService;
	private final WriteCommentService writeCommentService;
	private final WriteLikeRepository writeLikeRepository;

	@PostMapping("/my/write")
	public ResponseEntity<WriteDto> wirte(@ModelAttribute WriteDto writeDto,
			@RequestParam(name = "ifile", required = false) MultipartFile ifile,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			User user = principalDetails.getUser(); // jwt 인증 사용자
			Integer WriteId = writeService.writeDam(writeDto, ifile, user);
			WriteDto nWriteDto = writeService.detailWrite(WriteId);
			return new ResponseEntity<>(nWriteDto, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/writeDetail/{id}")
	public ResponseEntity<Map<String, Object>> detail(@PathVariable("id") Integer writeId,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			WriteDto nWriteDto = writeService.detailWrite(writeId);
			List<WriteCommentDto> comments = writeCommentService.findByWriteId(writeId);

	        boolean liked = false;
	        if (principalDetails != null) {
	            liked = writeService.isLiked(principalDetails.getUsername(), writeId);
	        }
	        
			Map<String, Object> res = new HashMap<>();
			res.put("write", nWriteDto);
			res.put("comments", comments);
	        res.put("liked", liked); // 좋아요 여부도 포함
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/my/comments")
	public ResponseEntity<?> saveComment(@RequestBody WriteCommentDto dto,
			@AuthenticationPrincipal PrincipalDetails principal) {
		try {
			if (principal == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			dto.setUsername(principal.getUsername()); // 작성자 주입
			writeCommentService.save(dto); // 실제 저장 로직

			return new ResponseEntity<>("댓글 등록 성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/writeList")
	public ResponseEntity<Map<String, Object>> getWriteList(@RequestBody WriteSearchRequestDto requestDto) {
//	    log.info("✅ 수신된 WriteSearchRequestDto: {}", requestDto);
//	    log.info(" - sort: {}", requestDto.getSort());
//	    log.info(" - type: {}", requestDto.getType());
//	    log.info(" - status: {}", requestDto.getStatus());
//	    log.info(" - keyword: {}", requestDto.getKeyword());
//	    log.info(" - page: {}", requestDto.getPage());
		int size = 10;
		Pageable pageable = PageRequest.of(requestDto.getPage() - 1, size);

		Page<Write> pageResult = writeService.searchWrites(requestDto, pageable);

		List<WriteDto> writeList = pageResult.getContent().stream().map(WriteDto::from).collect(Collectors.toList());

		PageInfo2 pageInfo = PageInfo2.from(pageResult);

		try {
			Map<String, Object> res = new HashMap<>();
			res.put("writeList", writeList);
			res.put("pageInfo", pageInfo);
			res.put("totalCount", pageResult.getTotalElements()); 
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	// 좋아요 여부 조회용
	@GetMapping("/write-likeCheck")
	public ResponseEntity<Boolean> checkLike(@AuthenticationPrincipal PrincipalDetails principalDetails,
	                                         @RequestParam Integer writeId) {
	    try {
	        String username = principalDetails.getUsername();
	        boolean liked = writeService.isLiked(username, writeId);
	        return ResponseEntity.ok(liked);
	    } catch (Exception e) {
	        log.error("좋아요 여부 확인 실패", e);
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
	@GetMapping("/write-likeCnt")
	public ResponseEntity<Integer> getLikeCount(@RequestParam Integer writeId) {
	    try {
	        int count = writeService.getLikeCount(writeId);
	        return ResponseEntity.ok(count);
	    } catch (Exception e) {
	        log.error("좋아요 수 확인 실패", e);
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/write-ViewCount")
	public ResponseEntity<Void> increaseViewCount(@RequestBody Map<String, Integer> body) {
	    try {
	        Integer writeId = body.get("writeId");
	        writeService.increaseViewCount(writeId);
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        log.error("❌ 조회수 증가 실패", e);
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/my/writeComment-adopt")
	public ResponseEntity<?> adoptComment(@RequestBody Map<String, Integer> req) {
		Integer writeCommentId = req.get("writeCommentId");
		try {
	        writeCommentService.adoptComment(writeCommentId);
	        return ResponseEntity.ok().build();
	    } catch (IllegalStateException e) {
	        log.error("중복 채택 오류", e);
	        return ResponseEntity
	                .status(HttpStatus.CONFLICT)
	                .body(e.getMessage());
	    } catch (Exception e) {
	        log.error("서버 오류", e);
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("서버 오류가 발생했습니다.");
	    }
	}
}
