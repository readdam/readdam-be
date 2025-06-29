package com.kosta.readdam.controller.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.UpdateLocationRequest;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.service.GeoService;
import com.kosta.readdam.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final GeoService geoService;

	@PutMapping("/user/location")
	public ResponseEntity<?> updateLocation(
	        @RequestBody UpdateLocationRequest request,
	        @AuthenticationPrincipal PrincipalDetails principalDetails
	) {
	    try {
	        if (principalDetails == null) {
	            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
	        }

	        UserDto userDto = userService.updateLocation(
	                principalDetails.getUsername(),
	                request.getLatitude(),
	                request.getLongitude()
	        );

	        return new ResponseEntity<>(userDto, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}
	
	@GetMapping("/user/location-address")
	public ResponseEntity<?> getUserLocationAddress(
	        @AuthenticationPrincipal PrincipalDetails principalDetails
	) {
	    try {
		    if (principalDetails == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                .body("로그인이 필요한 서비스입니다.");
		    }
	
		    UserDto userDto = userService.getUser(principalDetails.getUsername());
	
		    if (userDto.getLat() == null || userDto.getLng() == null) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                .body("사용자 위치 정보가 없습니다.");
		    }
	
	        String address = geoService.reverseGeocode(
	                userDto.getLat(),
	                userDto.getLng()
	        );
	
	        return ResponseEntity.ok(Map.of("address", address));
	
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("주소 변환 중 오류가 발생했습니다.");
	    }
	}
}