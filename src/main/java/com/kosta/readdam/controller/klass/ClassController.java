package com.kosta.readdam.controller.klass;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.service.klass.ClassService;

@RestController
public class ClassController {
	
	@Autowired
	private ClassService classService;
	
	@CrossOrigin(origins = "http://localhost:5173") 
	@PostMapping("/my/createClass")
	public ResponseEntity<ClassDto> createClass(@ModelAttribute ClassDto classDto, 
			@RequestParam(name = "mainImgF", required = false) MultipartFile mainImgF,
			@RequestParam(name = "leaderImgF", required = false) MultipartFile leaderImgF,
			@RequestParam(name = "round1ImgF", required = false) MultipartFile round1ImgF,
			@RequestParam(name = "round1BookimgF", required = false) MultipartFile round1BookimgF,
			@RequestParam(name = "round2ImgF", required = false) MultipartFile round2ImgF,
			@RequestParam(name = "round2BookimgF", required = false) MultipartFile round2BookimgF,
			@RequestParam(name = "round3ImgF", required = false) MultipartFile round3ImgF,
			@RequestParam(name = "round3BookimgF", required = false) MultipartFile round3BookimgF,
			@RequestParam(name = "round4ImgF", required = false) MultipartFile round4ImgF,
			@RequestParam(name = "round4BookimgF", required = false) MultipartFile round4BookimgF,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		Map<String, MultipartFile> imageMap = new HashMap<>();
		imageMap.put("mainImgF", mainImgF);
		imageMap.put("leaderImgF", leaderImgF);
		imageMap.put("round1ImgF", round1ImgF);
		imageMap.put("round1BookimgF", round1BookimgF);
		imageMap.put("round2ImgF", round2ImgF);
		imageMap.put("round2BookimgF", round2BookimgF);
		imageMap.put("round3ImgF", round3ImgF);
		imageMap.put("round3BookimgF", round3BookimgF);
		imageMap.put("round4ImgF", round4ImgF);
		imageMap.put("round4BookimgF", round4BookimgF);
		
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
	
	@GetMapping("/classDetail/{classId}")
	public ResponseEntity<Map<String,Object>> detail(@PathVariable("classId") Integer classId) {
//		System.out.println();
		try {
			ClassDto classDto = classService.detailClass(classId);
			Map<String,Object> res = new HashMap<>();
			res.put("data", classDto);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/api/classList")
	public Slice<ClassCardDto> getClassList(ClassSearchConditionDto condition,
			@PageableDefault(size=8) Pageable pageable){
		return classService.searchClasses(condition,pageable);
	}
	


}
