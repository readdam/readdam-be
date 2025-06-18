package com.kosta.readdam.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.service.ClassService;

@RestController
public class ClassController {
	
	@Autowired
	private ClassService classService;
	
	@PostMapping("/my/createClass")
	public ResponseEntity<ClassDto> createClass(@ModelAttribute ClassDto classDto, 
			@RequestParam("mainImg") MultipartFile mainImg,
			@RequestParam("leaderImg") MultipartFile leaderImg,
			@RequestParam("round1Img") MultipartFile round1Img,
			@RequestParam("round1Bookimg") MultipartFile round1Bookimg,
			@RequestParam("round2Img") MultipartFile round2Img,
			@RequestParam("round2Bookimg") MultipartFile round2Bookimg,
			@RequestParam("round3Img") MultipartFile round3Img,
			@RequestParam("round3Bookimg") MultipartFile round3Bookimg,
			@RequestParam("round4Img") MultipartFile round4Img,
			@RequestParam("round4Bookimg") MultipartFile round4Bookimg,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		Map<String, MultipartFile> imageMap = new HashMap<>();
		imageMap.put("mainImg", mainImg);
		imageMap.put("leaderImg", leaderImg);
		imageMap.put("round1Img", round1Img);
		imageMap.put("round1Bookimg", round1Bookimg);
		imageMap.put("round2Img", round2Img);
		imageMap.put("round2Bookimg", round2Bookimg);
		imageMap.put("round3Img", round3Img);
		imageMap.put("round3Bookimg", round3Bookimg);
		imageMap.put("round4Img", round4Img);
		imageMap.put("round4Bookimg", round4Bookimg);
		
		try {
			User leader = principalDetails.getUser(); //jwt 인증 사용자
			Integer classId = classService.createClass(classDto, imageMap, leader);
			ClassDto nClassDto = classService.detailClass(classId);
			return new ResponseEntity<>(nClassDto, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}

}
