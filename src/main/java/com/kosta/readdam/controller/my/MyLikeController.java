package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BookDto;
import com.kosta.readdam.service.my.MyBookLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyLikeController {

    private final MyBookLikeService myBookLikeService;

    @GetMapping("/likeBook")
    public ResponseEntity<List<BookDto>> getLikedBooks(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws Exception {
        String username = principalDetails.getUsername();
        List<BookDto> likedBooks = myBookLikeService.getLikedBooksByUsername(username);
        return ResponseEntity.ok(likedBooks);
    }
}
