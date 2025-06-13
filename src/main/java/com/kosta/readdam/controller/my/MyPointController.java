package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.PointDto;
import com.kosta.readdam.service.my.MyPointService;

@RestController
public class MyPointController {

	@Autowired
	private MyPointService myPointService;

	// 나의 포인트 내역 조회
	
	@PostMapping("/myPointList")
	public ResponseEntity<?> getMyPointList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		if (principalDetails == null) {
			return new ResponseEntity<>("인증 정보 없음", HttpStatus.UNAUTHORIZED); // 401
		}
		try {
			String username = principalDetails.getUsername();
			List<PointDto> pointList = myPointService.getMyPointList(username);
			return new ResponseEntity<>(pointList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		}
	}
	
	   @PostMapping("/myPointCharge")
	    public ResponseEntity<?> confirmChargePoint(
	            @RequestParam String paymentKey,
	            @RequestParam String orderId,
	            @RequestParam int amount,
	            @AuthenticationPrincipal PrincipalDetails principalDetails) {

	        if (principalDetails == null) {
	            return new ResponseEntity<>("인증 정보 없음", HttpStatus.UNAUTHORIZED);
	        }

	        try {
	            String username = principalDetails.getUsername();
	            myPointService.confirmAndChargePoint(paymentKey, orderId, amount, username);
	            return new ResponseEntity<>("충전 성공", HttpStatus.OK);
	        } catch (IllegalArgumentException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("충전 처리 실패", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

}
