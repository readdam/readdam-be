package com.kosta.readdam.controller.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	@PostMapping("/user/write")
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
	

}
