package com.kosta.readdam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.CreateReportRequest;
import com.kosta.readdam.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/my/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    
    @PostMapping
    public ResponseEntity<Void> createReport(
        @AuthenticationPrincipal PrincipalDetails principal,
        @RequestBody CreateReportRequest req
    ) {
        reportService.saveReport(principal.getUsername(), req);
        return ResponseEntity.ok().build();
    }
}