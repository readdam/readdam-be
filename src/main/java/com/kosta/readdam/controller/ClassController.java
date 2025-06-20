package com.kosta.readdam.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	
	@CrossOrigin(origins = "http://localhost:5173") 
	@PostMapping("/my/createClass")
	public ResponseEntity<ClassDto> createClass(@ModelAttribute ClassDto classDto, 
			@RequestParam(value = "mainImg", required = false) MultipartFile mainImg,
			@RequestParam(value = "leaderImg", required = false) MultipartFile leaderImg,
			@RequestParam(value = "round1Img", required = false) MultipartFile round1Img,
			@RequestParam(value = "round1Bookimg", required = false) MultipartFile round1Bookimg,
			@RequestParam(value = "round2Img", required = false) MultipartFile round2Img,
			@RequestParam(value = "round2Bookimg", required = false) MultipartFile round2Bookimg,
			@RequestParam(value = "round3Img", required = false) MultipartFile round3Img,
			@RequestParam(value = "round3Bookimg", required = false) MultipartFile round3Bookimg,
			@RequestParam(value = "round4Img", required = false) MultipartFile round4Img,
			@RequestParam(value = "round4Bookimg", required = false) MultipartFile round4Bookimg,
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
