package com.kosta.readdam.controller.home;

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

			List<UnifiedPlaceDto> result = null;

			if ("latest".equalsIgnoreCase(sort)) {
				// 최신순
				List<UnifiedPlaceDto> otherPlaces = homePlaceService.getLatestOtherPlaces(limit);
				List<UnifiedPlaceDto> placeList = homePlaceService.getLatestPlaces(limit);

				result = new java.util.ArrayList<>();
				result.addAll(otherPlaces);
				result.addAll(placeList);
				
				// 중복제거 추가
				result = result.stream()
				        .collect(Collectors.toMap(
				                dto -> dto.getType() + "-" + dto.getId(), 
				                dto -> dto,
				                (dto1, dto2) -> dto1
				        ))
				        .values()
				        .stream()
				        .collect(Collectors.toList());

				// 정렬: id DESC + 외부 우선
				result.sort(java.util.Comparator
						.comparing(UnifiedPlaceDto::getId,
								java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()))
						.thenComparing(dto -> "OTHER".equals(dto.getType()) ? 0 : 1));

				if (result.size() > limit) {
					result = result.subList(0, limit);

				}
			} else if ("distance".equalsIgnoreCase(sort)) {
				if (lat == null || lng == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("위치 기반 정렬에는 lat, lng 파라미터가 필요합니다.");
				}

				List<UnifiedPlaceDto> otherPlaces = homePlaceService.getOtherPlacesByDistance(lat, lng, limit);

				int remain = limit - otherPlaces.size();
				List<UnifiedPlaceDto> placeList = new java.util.ArrayList<>();

				if (remain > 0) {
					placeList = homePlaceService.getPlacesByDistance(lat, lng, remain);

					for (UnifiedPlaceDto dto : placeList) {
						if (dto.getLat() != null && dto.getLng() != null) {
							dto.setDistanceKm(com.kosta.readdam.util.DistanceUtil.calculateDistanceKm(lat, lng,
									dto.getLat(), dto.getLng()));
						} else {
							dto.setDistanceKm(Double.MAX_VALUE);
						}
					}
				}

				result = new java.util.ArrayList<>();
				result.addAll(otherPlaces);
				result.addAll(placeList);
				
				// 중복제거 추가
				result = result.stream()
				        .collect(Collectors.toMap(
				                dto -> dto.getType() + "-" + dto.getId(), 
				                dto -> dto,
				                (dto1, dto2) -> dto1
				        ))
				        .values()
				        .stream()
				        .collect(Collectors.toList());

				// 정렬: 외부 우선 → 거리순
				result.sort(
						java.util.Comparator.comparing((UnifiedPlaceDto dto) -> "OTHER".equals(dto.getType()) ? 0 : 1)
								.thenComparing(UnifiedPlaceDto::getDistanceKm,
										java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));

			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원하지 않는 정렬 방식입니다: " + sort);
			}

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("홈 장소 데이터를 불러오던 중 오류가 발생했습니다.");
		}
	}

	// 모임 최신순 4개 가져오기
	@GetMapping("/classes")
	public ResponseEntity<?> getHomeClasses(@RequestParam(defaultValue = "latest") String sort,
			@RequestParam(defaultValue = "4") int limit, @RequestParam(required = false) Double lat,
			@RequestParam(required = false) Double lng) {
		try {
			if ("latest".equalsIgnoreCase(sort)) {
				List<ClassDto> latestClasses = classService.getLatestClasses();
				return ResponseEntity.ok(latestClasses);
			}

			/*
			 * // 위치 기반 정렬 (추후 사용 예정) else if ("location".equalsIgnoreCase(sort)) { if (lat
			 * == null || lng == null) { return ResponseEntity
			 * .status(HttpStatus.BAD_REQUEST) .body("위치 기반 정렬은 lat, lng 파라미터가 필요합니다."); }
			 * List<ClassDto> result = classService.getClassesByDistance(lat, lng, limit);
			 * return ResponseEntity.ok(result); }
			 */

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원하지 않는 정렬 방식입니다: " + sort);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("홈 모임 데이터를 불러오던 중 오류가 발생했습니다.");
		}
	}
	@GetMapping("/banner")
    public ResponseEntity<BannerDto> getHomeBanner() {
        BannerDto bannerDto = bannerService.getHomeBanner();
        return ResponseEntity.ok(bannerDto);
    }
}
