package com.kosta.readdam.controller.write;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kosta.readdam.service.write.WriteCommentService;
import com.kosta.readdam.service.write.WriteService;
import com.kosta.readdam.util.PageInfo2;
@RestController

public class WriteController {
	@Autowired
	private WriteService writeService;
	
	@Autowired
	private WriteCommentService writeCommentService;
	
	@PostMapping("/my/write")
	public ResponseEntity<WriteDto> wirte(@ModelAttribute WriteDto writeDto,
			@RequestParam(name="ifile", required=false) MultipartFile ifile, 
	        @AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			User user = principalDetails.getUser(); //jwt 인증 사용자
			Integer WriteId = writeService.writeDam(writeDto, ifile, user);
			WriteDto nWriteDto = writeService.detailWrite(WriteId);
			return new ResponseEntity<>(nWriteDto, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/writeDetail/{id}")
	public ResponseEntity<Map<String,Object>> detail(
	        @PathVariable("id") Integer writeId, 
	        @AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			WriteDto nWriteDto = writeService.detailWrite(writeId);
	        List<WriteCommentDto> comments = writeCommentService.findByWriteId(writeId);

			Map<String,Object> res = new HashMap<>();
			res.put("write", nWriteDto);
			res.put("comments", comments);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/my/comments")
	public ResponseEntity<?> saveComment(
	    @RequestBody WriteCommentDto dto,
	    @AuthenticationPrincipal PrincipalDetails principal
	) {
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
        	    .map(WriteDto::from)
        	    .collect(Collectors.toList());

        PageInfo2 pageInfo = PageInfo2.from(pageResult);

        Map<String, Object> res = new HashMap<>();
        res.put("writeList", writeList);
        res.put("pageInfo", pageInfo);
        return ResponseEntity.ok(res);
    }
}
