package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.service.my.MyWriteShortService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyWriteShortController {

    private final MyWriteShortService myWriteShortService;


    @GetMapping("/myWriteShort")
    public List<WriteShortDto> getMyWriteShorts(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        return myWriteShortService.getMyWriteShorts(username);
    }


    @PostMapping("/myLikeShort/{id}")
    public WriteShortDto toggleLike(
            @PathVariable("id") Integer writeshortId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        String username = principalDetails.getUsername();
        return myWriteShortService.toggleLike(writeshortId, username);
    }
}
