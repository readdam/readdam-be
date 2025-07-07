package com.kosta.readdam.controller.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.service.UserService;
import com.kosta.readdam.service.admin.AdminAlertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/alert")
@RequiredArgsConstructor
public class AdminAlertController {

    private final AdminAlertService adminAlertService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${iupload.path}")
    private String iuploadPath;

    @PostMapping
    public ResponseEntity<Void> createAlerts(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(value = "usernames", required = false) String usernamesJson,
            @RequestParam("sendToAll") boolean sendToAll,
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "linkUrl", required = false) String linkUrl,
            @RequestParam(value = "scheduledTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime scheduledTime,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        // 1) 발신자 username
        String senderUsername = principal.getUsername();

        // 2) usernamesJson → List<String>
        List<String> usernames = List.of();
        if (!sendToAll && usernamesJson != null && !usernamesJson.isBlank()) {
            usernames = objectMapper.readValue(
                usernamesJson,
                new TypeReference<List<String>>() {}
            );
        }

        // 3) 이미지 저장
        String imageName = null;
        if (image != null && !image.isEmpty()) {
            File uploadDir = new File(iuploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String originalName = image.getOriginalFilename();
            String ext = (originalName != null && originalName.contains("."))
                         ? originalName.substring(originalName.lastIndexOf("."))
                         : "";
            imageName = UUID.randomUUID().toString() + ext;

            File target = new File(iuploadPath, imageName);
            try (FileOutputStream fos = new FileOutputStream(target)) {
                fos.write(image.getBytes());
            }
        }

        // 4) 서비스 호출
        adminAlertService.sendCustomAlerts(
            senderUsername,
            usernames,
            type,
            title,
            content,
            linkUrl,
            scheduledTime,
            imageName,
            sendToAll
        );

        // 5) 응답
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> search(
            @RequestParam(value = "q", required = false) String q
    ) {
        return ResponseEntity.ok(userService.search(q));
    }
}
