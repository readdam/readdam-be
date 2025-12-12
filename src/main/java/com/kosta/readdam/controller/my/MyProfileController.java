package com.kosta.readdam.controller.my;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.service.my.MyProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my")
public class MyProfileController {

	@Autowired
    private MyProfileService myProfileService;

    @Value("${iupload.path}")
    private String iuploadPath;

    // 🔹 프로필 정보 가져오기
    @PostMapping("/myProfile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(401).body("인증 정보 없음");
        }

        try {
            String username = principalDetails.getUsername();
            UserDto userDto = myProfileService.getMyProfile(username);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("프로필 조회 실패");
        }
    }

    // 🔹 프로필 정보 업데이트
    @PostMapping("/myProfileEdit")
    public ResponseEntity<?> updateMyProfile(@RequestBody UserDto dto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(401).body("인증 정보 없음");
        }

        try {
            dto.setUsername(principalDetails.getUsername());
            myProfileService.updateMyProfile(dto);
            return ResponseEntity.ok("수정 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("프로필 수정 실패");
        }
    }

    // 🔹 프로필 이미지 업로드
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            File uploadDir = new File(iuploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + ext;

            File target = new File(iuploadPath, savedName);
            try (FileOutputStream fos = new FileOutputStream(target)) {
                fos.write(file.getBytes());
            }

            return ResponseEntity.ok(savedName); // 프론트는 이 이름을 dto.profileImg로 전달
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("이미지 업로드 실패");
        }
    }
    
    @PostMapping("/myWithdrawal")
    public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @RequestBody Map<String, String> body) {
        if (principalDetails == null) {
            return ResponseEntity.status(401).body("인증 정보 없음");
        }

        try {
            String username = principalDetails.getUsername();
            String reason = body.get("reason");

            myProfileService.withdrawUser(username, reason);

            return ResponseEntity.ok("탈퇴 처리 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("탈퇴 처리 실패");
        }
    }

}
