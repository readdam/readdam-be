package com.kosta.readdam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.service.ClassService;

@RestController
public class MeetingController {
	
	@Autowired
	private ClassService classService;
	
	@PostMapping("/class/create")
	public ResponseEntity<ClassDto> createClass(ClassDto classDto, 
			@RequestParam(name="ifile", required = false) MultipartFile ifile,
			@RequestParam(name="dfile", required = false) MultipartFile dfile) {
		try {
			Integer classId = classService.createClass(classDto, ifile, dfile);
			ClassDto nClassDto = classService.detailClass(classId);
			return new ResponseEntity<>(nClassDto, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}

}
