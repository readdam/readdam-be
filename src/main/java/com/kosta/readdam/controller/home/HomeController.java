package com.kosta.readdam.controller.home;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.BannerDto;
import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.service.BannerService;
import com.kosta.readdam.service.klass.ClassService;
import com.kosta.readdam.service.place.HomePlaceService;
import com.kosta.readdam.service.write.WriteService;
import com.kosta.readdam.service.write.WriteShortService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

	private final WriteService writeService;
	private final WriteShortService writeShortService;
	private final HomePlaceService homePlaceService;
	private final ClassService classService;
    private final BannerService bannerService;

	// 글쓰기 최신순 4개 가져오기
	@GetMapping("/writes")
	public ResponseEntity<?> getLatestWrites(@RequestParam(defaultValue = "4") int limit) {
		try {
			List<WriteDto> writes = writeService.findLatest(limit);
			return ResponseEntity.ok(writes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글쓰기 최신 데이터를 불러오던 중 오류가 발생했습니다.");
		}
	}

	// 읽담한줄 최신순 5개 가져오기
	@GetMapping("/shorts")
	public ResponseEntity<?> getLatestWriteShorts(@RequestParam(defaultValue = "5") int limit,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {
		try {
			String username = (principalDetails != null) ? principalDetails.getUsername() : null;
			List<WriteShortDto> writeShorts = writeShortService.findLatest(limit, username);
			return ResponseEntity.ok(writeShorts);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("읽담한줄 최신 데이터를 불러오던 중 오류가 발생했습니다.");
		}
	}

	@GetMapping("/places")
	public ResponseEntity<?> getHomePlaces(
			@RequestParam(defaultValue = "latest") String sort,
			@RequestParam(defaultValue = "4") int limit, 
			@RequestParam(required = false) Double lat,
			@RequestParam(required = false) Double lng) {
	    try {
	        List<UnifiedPlaceDto> places;

	        if ("latest".equalsIgnoreCase(sort)) {
	            places = homePlaceService.getLatestPlaces(limit);
	        } else if ("distance".equalsIgnoreCase(sort)) {
	            if (lat == null || lng == null) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("위치 기반 정렬에는 lat, lng 파라미터가 필요합니다.");
	            }
	            places = homePlaceService.getPlacesByDistance(lat, lng, limit);
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("지원하지 않는 정렬 방식입니다: " + sort);
	        }

	        return ResponseEntity.ok(places);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("홈 장소 데이터를 불러오던 중 오류가 발생했습니다.");
	    }
	}

	// 모임 4개 가져오기 (최신순/거리순)
	@GetMapping("/classes")
	public ResponseEntity<?> getHomeClasses(
			@RequestParam(defaultValue = "latest") String sort,
			@RequestParam(defaultValue = "4") int limit, 
			@RequestParam(required = false) Double lat,
			@RequestParam(required = false) Double lng) {
		try {
	        List<ClassCardDto> classes;
	        if ("latest".equalsIgnoreCase(sort)) {
	            classes = classService.getLatestClasses(limit);
	        } else if ("distance".equalsIgnoreCase(sort)) {
	            if (lat == null || lng == null) {
	                return ResponseEntity.badRequest()
	                        .body("위치 기반 정렬에는 lat, lng 파라미터가 필요합니다.");
	            }
	            classes = classService.getClassesByDistance(lat, lng, limit);
	        } else {
	            return ResponseEntity.badRequest().body("지원하지 않는 정렬 방식입니다: " + sort);
	        }
	        return ResponseEntity.ok(classes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("홈 모임 데이터를 불러오던 중 오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/banner")
    public ResponseEntity<BannerDto> getHomeBanner() {
        BannerDto bannerDto = bannerService.getHomeBanner();
        return ResponseEntity.ok(bannerDto);
    }
}
