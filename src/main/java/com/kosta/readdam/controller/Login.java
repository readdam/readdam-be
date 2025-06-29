package com.kosta.readdam.controller;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.service.UserService;

@RestController
public class Login {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/user")
	public ResponseEntity<UserDto> user(
	    @AuthenticationPrincipal PrincipalDetails principalDetails,
	    @RequestBody Map<String, String> requestBody // ✅ fcmToken 받기
	) {
	    try {
	        User user = principalDetails.getUser();

	        String fcmToken = requestBody.get("fcmToken");
	        if (fcmToken != null && !fcmToken.isBlank()) {
	            user.setFcmToken(fcmToken);
	        }
	        System.out.println("✅ 받은 fcmToken: " + fcmToken);

	        user.setPassword(""); // 비밀번호 비우기 (보안)
	        userRepository.save(user); // ✅ 저장 반영

	        return new ResponseEntity<>(user.toDto(), HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

	
	@PostMapping("/join")
	public ResponseEntity<Boolean> join(
	        @RequestPart("userDto") UserDto userdto,
	        @RequestPart(value = "file", required = false) MultipartFile file
	) {
		try {
//			userdto.setPassword(bCryptPasswordEncoder.encode(userdto.getPassword()));
			userService.join(userdto, file);
			return new ResponseEntity<>(true, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
