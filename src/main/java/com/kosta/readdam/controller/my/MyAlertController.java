package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.service.my.MyAlertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyAlertController {

    private final MyAlertService myAlertService;

    @PostMapping("/myAlertList")
    public ResponseEntity<PagedResponse<AlertDto>> getAlertList(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<AlertDto> result = myAlertService.getMyAlerts(principal.getUsername(), page, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/myAlertCheck")
    public ResponseEntity<Void> checkAlert(@RequestBody AlertDto dto) throws Exception {
        myAlertService.checkAlert(dto.getAlertId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread(
            @AuthenticationPrincipal PrincipalDetails principal) {

        return ResponseEntity.ok(myAlertService.countUnread(principal.getUsername()));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<AlertDto>> latestAlerts(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(defaultValue = "2") int limit) {

        return ResponseEntity.ok(
                myAlertService.getLatestAlerts(principal.getUsername(), limit));
    }
}
