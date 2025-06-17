package com.kosta.readdam.controller.write;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.service.WriteService;
@RestController
public class WriteController {
	@Autowired
	private WriteService writeService;
	
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
	
	@GetMapping("/writedetail/{writeId}")
	public ResponseEntity<Map<String,Object>> detail(
	        @PathVariable("writeId") Integer writeId, 
	        @AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			WriteDto nWriteDto = writeService.detailWrite(writeId);
			Map<String,Object> res = new HashMap<>();
			res.put("write", nWriteDto);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}	
}
