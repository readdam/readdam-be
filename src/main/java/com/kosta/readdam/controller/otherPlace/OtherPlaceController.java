package com.kosta.readdam.controller.otherPlace;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.service.FileService;
import com.kosta.readdam.service.otherPlace.OtherPlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class OtherPlaceController {

	private final FileService fileService;
	private final OtherPlaceService otherPlaceService;
	
	
	@PostMapping(value = "/otherPlaceAdd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> registerOtherPlace(
	        @RequestPart("placeDto") OtherPlaceDto placeDto,
	        @RequestPart(value = "images", required = false) List<MultipartFile> images,
	        @RequestPart(value = "keywords", required = false) String keywordsJson
	) throws Exception {

	    // 1. 이미지 저장
	    if (images != null) {
	        List<String> savedPaths = fileService.save(images); // 예: /uploads/xxx.jpg
	        for (int i = 0; i < savedPaths.size(); i++) {
	            Field field = OtherPlaceDto.class.getDeclaredField("img" + (i + 1));
	            field.setAccessible(true);
	            field.set(placeDto, savedPaths.get(i));
	        }
	    }

	    // 2. 키워드 저장
	    if (keywordsJson != null && !keywordsJson.isEmpty()) {
	    	ObjectMapper objectMapper = new ObjectMapper();
	        List<String> keywords = objectMapper.readValue(keywordsJson, new TypeReference<List<String>>() {});

	        for (int i = 0; i < keywords.size(); i++) {
	            Field field = OtherPlaceDto.class.getDeclaredField("tag" + (i + 1));
	            field.setAccessible(true);
	            field.set(placeDto, keywords.get(i));
	        }
	    }

	    // 3. 저장
	    otherPlaceService.save(placeDto);
	    return ResponseEntity.ok("외부 장소 저장 완료");
	}

	@GetMapping("/otherPlaceList")
	public ResponseEntity<Page<OtherPlaceSummaryDto>> getOtherPlaceList(
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "10") int size,
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "filterBy", required = false) String filterBy
	) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<OtherPlaceSummaryDto> placePage = otherPlaceService.getOtherPlaceList(pageable, keyword, filterBy);
	    return ResponseEntity.ok(placePage);
	}

	@GetMapping("/otherPlace/{id}")
	public ResponseEntity<OtherPlaceDto> getOtherPlaceDetail(@PathVariable Integer id) {
		OtherPlaceDto dto = otherPlaceService.getOtherPlaceDetail(id);
	    return ResponseEntity.ok(dto);
	}
	
	@PostMapping(value = "/otherPlaceEdit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Transactional
	public ResponseEntity<String> editOtherPlace(
	    @PathVariable Integer id,
	    @RequestPart("placeDto") OtherPlaceDto dto,
	    @RequestPart(value = "placeImages", required = false) List<MultipartFile> newImages,
	    @RequestPart(value = "existingImages", required = false) List<String> existingImages,
	    @RequestPart(value = "keywords", required = false) String keywordsJson
	) throws Exception {

	    // 1. 이미지 저장
	    List<String> saved = (newImages != null) ? fileService.save(newImages) : new ArrayList<>();
	    List<String> all = new ArrayList<>();
	    if (existingImages != null) all.addAll(existingImages);
	    all.addAll(saved);

	    for (int i = 0; i < 5; i++) {
	        Field f = OtherPlaceDto.class.getDeclaredField("img" + (i + 1));
	        f.setAccessible(true);
	        f.set(dto, i < all.size() ? all.get(i) : null);
	    }

	    // 2. 태그 세팅
	    if (keywordsJson != null && !keywordsJson.isBlank()) {
	        ObjectMapper mapper = new ObjectMapper();
	        List<String> tags = mapper.readValue(keywordsJson, new TypeReference<List<String>>() {});
	        for (int i = 0; i < 5; i++) {
	            Field f = OtherPlaceDto.class.getDeclaredField("tag" + (i + 1));
	            f.setAccessible(true);
	            f.set(dto, i < tags.size() ? tags.get(i) : null);
	        }
	    }

	    // 3. 서비스에 DTO만 전달
	    otherPlaceService.updateOtherPlace(id, dto);

	    return ResponseEntity.ok("수정 완료");
	}


}
