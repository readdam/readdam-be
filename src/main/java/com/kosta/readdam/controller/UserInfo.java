package com.kosta.readdam.controller;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfo {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/userInfo")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(401).body("인증 정보 없음");
        }

        User user = userRepository.findById(principalDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        UserDto dto = user.toDto(); // 비밀번호 null 처리 포함됨
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }
}
