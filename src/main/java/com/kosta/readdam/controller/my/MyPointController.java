package com.kosta.readdam.controller.my;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.ChargeRequest;
import com.kosta.readdam.dto.PointDto;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.service.my.MyPointService;
import com.kosta.readdam.util.TossService;

@RestController
@RequestMapping("/my")
public class MyPointController {

	@Autowired
	private MyPointService myPointService;

	@Autowired
	private TossService tossService;

	// 나의 포인트 내역 조회

	@PostMapping("/myPointList")
	public ResponseEntity<?> getMyPointList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		if (principalDetails == null) {
			return new ResponseEntity<>("인증 정보 없음", HttpStatus.UNAUTHORIZED); // 401
		}
		try {
			String username = principalDetails.getUsername();
			List<PointDto> pointList = myPointService.getMyPointList(username);
			int totalPoint = myPointService.getMyTotalPoint(username);

			Map<String, Object> result = Map.of("pointList", pointList, "totalPoint", totalPoint);

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/myPointBefore")
	public ResponseEntity<?> createOrder(
	        @AuthenticationPrincipal PrincipalDetails principal,
	        @RequestBody Map<String, Integer> body
	) {
	    if (principal == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
	    }

	    try {
	    	if (principal==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        int point = body.get("point");
	        int price = body.get("price");
	        String orderUuid = myPointService.createOrder(principal.getUser(), point, price);
	        return ResponseEntity.ok(Map.of("orderUuid", orderUuid));
	    } catch (IllegalArgumentException e) {
	        // 입력값 오류 등
	        return ResponseEntity.badRequest().body(e.getMessage());
	    } catch (Exception e) {
	        // 서버 에러
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("주문 생성 중 오류가 발생했습니다.");
	    }
	}


	@PostMapping("/myPointCharge")
	public ResponseEntity<?> verifyAndCharge(@AuthenticationPrincipal PrincipalDetails principal,
			@RequestBody ChargeRequest req) {
		System.out.println(">> verifyAndCharge 진입: " + req);
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
		}
		
		try {
			// 1) Toss 결제 검증
			tossService.verify(req.getPaymentKey(), req.getOrderId(), req.getAmount());
			System.out.println(">> tossService.verify 통과");
			// 2) 검증 통과 시 주문 상태 변경·포인트 적립·내역 저장
			 myPointService.verifyAndSave(req.getPaymentKey(), req.getOrderId(), req.getPoint(), principal.getUser());
			 System.out.println(">> verifyAndSave 통과");
			return ResponseEntity.ok(Map.of("status", "success"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 검증 또는 포인트 적립에 실패했습니다.");
		}
	}

	@GetMapping("/userInfo")
	public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal PrincipalDetails principal) {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		UserDto dto = principal.getUser().toDto();
		return ResponseEntity.ok(dto);
	}
}
