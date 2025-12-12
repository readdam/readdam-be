package com.kosta.readdam.controller.my;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassUserDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.service.my.MyClassUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my/classes")
@RequiredArgsConstructor
public class MyClassController {

    private final MyClassUserService myClassUserService;

    @GetMapping("/ongoing")
    public ResponseEntity<PagedResponse<ClassUserDto>> ongoing(
        @AuthenticationPrincipal UserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            myClassUserService.getOngoingClasses(user.getUsername(), page, size)
        );
    }

    @GetMapping("/past")
    public ResponseEntity<PagedResponse<ClassUserDto>> past(
        @AuthenticationPrincipal UserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            myClassUserService.getPastClasses(user.getUsername(), page, size)
        );
    }

    @GetMapping("/created")
    public ResponseEntity<PagedResponse<ClassDto>> created(
        @AuthenticationPrincipal UserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            myClassUserService.getCreatedClasses(user.getUsername(), page, size)
        );
    }
}