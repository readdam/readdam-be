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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.SpellCheckRequest;
import com.kosta.readdam.dto.SpellCheckResponse;
import com.kosta.readdam.dto.WriteCommentDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.service.write.SpellCheckService;
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
	private final WriteCommentRepository writeCommentRepository;
	private final SpellCheckService spellCheckService;
	
	@PostMapping("/my/write")
	public ResponseEntity<WriteDto> wirte(
			@ModelAttribute WriteDto writeDto,
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
		int size = 10;
		Pageable pageable = PageRequest.of(requestDto.getPage() - 1, size);

		Page<Write> pageResult = writeService.searchWrites(requestDto, pageable);

	    List<WriteDto> writeList = pageResult.getContent().stream()
	            .map(write -> {
	                WriteDto dto = write.toDto();
	                dto.setCommentCnt(write.getCommentCnt());
	                return dto;
	            })
	            .collect(Collectors.toList());

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
	        log.error("포인트 부족 오류", e);
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
	
	@PostMapping("/my/writeModify/{id}")
	public ResponseEntity<WriteDto> modify(
			@PathVariable("id") Integer writeId,
			@ModelAttribute WriteDto writeDto,
			@RequestParam(name = "ifile", required = false) MultipartFile ifile,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			writeDto.setWriteId(writeId);
			User user = principalDetails.getUser(); 
			writeService.modifyDam(writeDto, ifile, user);
			WriteDto nWriteDto = writeService.detailWrite(writeDto.getWriteId());
			return ResponseEntity.ok(nWriteDto);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/my/comments")
	public ResponseEntity<?> updateComment(
			@RequestBody WriteCommentDto dto,
			@AuthenticationPrincipal PrincipalDetails principal) {
		try {
			if (principal == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			dto.setUsername(principal.getUsername()); 
			writeCommentService.updateComment(dto, principal);

			return new ResponseEntity<>("댓글 수정 성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
//	--댓글 삭제--
//	- DELETE 요청이 아니라, PUT /my/writeComment-hide 사용
//	- 실제 삭제는 아니고 isHide = true 로 상태만 변경
//	- 채택된 댓글은 삭제 불가 → 409 CONFLICT 응답
//	- 삭제 후 commentCnt 감소
//	- 유저한테 표출하는 리스트는 숨겨진 댓글 제외하여 응답
	@PutMapping("/my/writeComment-hide")
	public ResponseEntity<?> delete_hideComment(
	        @RequestBody Map<String, Integer> req,
	        @AuthenticationPrincipal PrincipalDetails principal) {
	    try {
	        if (principal == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	        }

	        Integer commentId = req.get("writeCommentId");
	        writeCommentService.hideComment(commentId, principal);

	        return ResponseEntity.ok("댓글 삭제(숨김) 성공");

	    } catch (IllegalStateException e) {
	        log.error("댓글 숨김 실패", e);
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
    @PostMapping("/write-spellcheck")
    public ResponseEntity<SpellCheckResponse> checkSpelling(@RequestBody SpellCheckRequest request) {
        
    	log.info("맞춤법 검사 요청 text = {}", request.getText());

        SpellCheckResponse response = new SpellCheckResponse();

        try {
            response = spellCheckService.checkSpelling(request);
        } catch (Exception e) {
            log.error("맞춤법 검사 Controller 처리 중 오류", e);
            response.setErrorMessage("맞춤법 검사 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(response);
    }

	
}
