//package com.kosta.readdam.controller.my;
//
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kosta.readdam.config.auth.PrincipalDetails;
//import com.kosta.readdam.dto.WriteShortDto;
//import com.kosta.readdam.service.my.MyWriteShortService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/my")
//@RequiredArgsConstructor
//public class MyWriteShortController {
//
//    private final MyWriteShortService myWriteShortService;
//
//    /**
//     * 내가 작성한 짧은 글 목록 조회
//     */
//    @GetMapping("/writeShort")
//    public ResponseEntity<List<WriteShortDto>> getMyShorts(
//            @AuthenticationPrincipal PrincipalDetails principal
//    ) throws Exception {
//        String username = principal.getUsername();
//        List<WriteShortDto> list = myWriteShortService.getMyShorts(username);
//        return ResponseEntity.ok(list);
//    }
//
//
//    @PostMapping("/myLikeShort/{writeshortId}")
//    public ResponseEntity<Long> toggleLike(
//            @AuthenticationPrincipal PrincipalDetails principal,
//            @PathVariable Integer writeshortId
//    ) throws Exception {
//        String username = principal.getUsername();
//        long newCount = myWriteShortService.toggleLike(username, writeshortId);
//        return ResponseEntity.ok(newCount);
//    }
//}
