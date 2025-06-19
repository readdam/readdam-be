package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.service.my.MyAlertService;

@RestController
@RequestMapping("/my")
public class MyAlertController {

    @Autowired
    private MyAlertService myAlertService;

    // 알림 전체 조회
    @PostMapping("/myAlertList")
    public ResponseEntity<List<AlertDto>> getAlertList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            String username = principalDetails.getUsername();
            List<AlertDto> alerts = myAlertService.getMyAlerts(username);
            return new ResponseEntity<>(alerts, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 알림 클릭 시 isChecked 처리
    @PostMapping("/myAlertCheck")
    public ResponseEntity<Boolean> checkAlert(@RequestBody AlertDto alertDto) {
        try {
            myAlertService.checkAlert(alertDto.getAlertId());
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
}
