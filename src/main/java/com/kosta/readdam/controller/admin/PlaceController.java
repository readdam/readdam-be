package com.kosta.readdam.controller.admin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.PlaceRoomDto;
import com.kosta.readdam.dto.PlaceTimeDto;
import com.kosta.readdam.dto.place.PlaceEditResponseDto;
import com.kosta.readdam.dto.place.PlaceSummaryDto;
import com.kosta.readdam.service.FileService;
import com.kosta.readdam.service.place.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;
    private final FileService fileService;

    @PostMapping(value = "/placeAdd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerPlaceWithFiles(
            @RequestPart("placeDto") PlaceDto placeDto,
            @RequestPart("roomDtoList") List<PlaceRoomDto> roomDtoList,
            @RequestPart("sharedTimeSlots") List<PlaceTimeDto> sharedTimeSlots,
            @RequestPart("placeImages") List<MultipartFile> placeImages,
            @RequestPart("roomImagesMap") List<MultipartFile> roomImagesFlat 
    ) throws Exception {

        // 1. 장소 이미지 저장 → placeDto.img1 ~ img10 설정
        List<String> savedPlacePaths = fileService.save(placeImages);
        for (int i = 0; i < savedPlacePaths.size(); i++) {
            Field field = PlaceDto.class.getDeclaredField("img" + (i + 1));
            field.setAccessible(true);
            field.set(placeDto, savedPlacePaths.get(i));
        }

        // 2. 각 방 이미지 저장 → 각 PlaceRoomDto.img1 ~ img10 설정
        Map<Integer, List<MultipartFile>> grouped = new HashMap<>();
        for (MultipartFile file : roomImagesFlat) {
            String original = file.getOriginalFilename(); // 예: room_0_2.jpg
            if (original != null && original.startsWith("room_")) {
                String[] parts = original.split("_");
                int roomIndex = Integer.parseInt(parts[1]);
                grouped.computeIfAbsent(roomIndex, k -> new ArrayList<>()).add(file);
            }
        }

        for (int i = 0; i < roomDtoList.size(); i++) {
            PlaceRoomDto roomDto = roomDtoList.get(i);
            List<MultipartFile> files = grouped.get(i);
            if (files != null) {
                List<String> saved = fileService.save(files);
                for (int j = 0; j < saved.size(); j++) {
                    Field field = PlaceRoomDto.class.getDeclaredField("img" + (j + 1));
                    field.setAccessible(true);
                    field.set(roomDto, saved.get(j));
                }
            }
        }

        // 3. 서비스 실행
        placeService.registerPlace(placeDto, roomDtoList, sharedTimeSlots);
        return ResponseEntity.ok("장소 등록 완료");
    }
    
    @GetMapping(value = "/placeList")
    public ResponseEntity<Page<PlaceSummaryDto>> getPlaceList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String filterBy
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(placeService.getPlaceList(pageable, keyword, filterBy));
    }
    
    @GetMapping("/place/{placeId}")
    public ResponseEntity<PlaceEditResponseDto> getPlaceDetail(@PathVariable Integer placeId) {
        return ResponseEntity.ok(placeService.getPlaceDetail(placeId));
    }

    @Transactional
    @PostMapping(value = "/placeEdit/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editPlaceWithFiles(
            @PathVariable Integer placeId,
            @RequestPart("placeDto") PlaceDto placeDto,
            @RequestPart("roomDtoList") List<PlaceRoomDto> roomDtoList,
            @RequestPart("sharedTimeSlots") List<PlaceTimeDto> sharedTimeSlots,
            @RequestPart(value = "placeImages", required = false) List<MultipartFile> placeImages,
            @RequestPart(value = "roomImagesMap", required = false) List<MultipartFile> roomImagesFlat,
            @RequestPart(value = "existingPlaceImages", required = false) List<String> existingPlaceImages,
            @RequestPart(value = "existingRoomImages", required = false) List<String> existingRoomImages
    ) throws Exception {

        placeDto.setPlaceId(placeId);

        // 1. 장소 이미지 병합
        List<String> newPlaceImagePaths = (placeImages != null) ? fileService.save(placeImages) : new ArrayList<>();

        List<String> totalPlaceImages = new ArrayList<>();
        if (existingPlaceImages != null) totalPlaceImages.addAll(existingPlaceImages);
        totalPlaceImages.addAll(newPlaceImagePaths); // 기존 → 신규 순서

        for (int i = 0; i < totalPlaceImages.size(); i++) {
            Field field = PlaceDto.class.getDeclaredField("img" + (i + 1));
            field.setAccessible(true);
            field.set(placeDto, totalPlaceImages.get(i));
        }

        // 2. 방 이미지 병합
        // ✅ 신규 방 roomIndex 기반으로 이미지 매핑
        Map<Integer, List<MultipartFile>> roomImageMapByIndex = new HashMap<>();
        if (roomImagesFlat != null) {
            for (MultipartFile file : roomImagesFlat) {
                String filename = file.getOriginalFilename(); // 예: room_0_2.jpg
                if (filename != null && filename.startsWith("room_")) {
                    String[] parts = filename.split("_");
                    try {
                        int roomIndex = Integer.parseInt(parts[1]); // index 기반
                        roomImageMapByIndex.computeIfAbsent(roomIndex, k -> new ArrayList<>()).add(file);
                    } catch (NumberFormatException e) {
                        System.out.println("room index parse 실패: " + filename);
                    }
                }
            }
        }

        // ✅ 기존 이미지 (roomId 기반) 매핑
        Map<Integer, List<String>> existingRoomImageMap = new HashMap<>();
        if (existingRoomImages != null) {
            for (String path : existingRoomImages) {
                if (path.contains("|")) {
                    String[] parts = path.split("\\|");
                    int roomId = Integer.parseInt(parts[0]);
                    String imagePath = parts[1];
                    existingRoomImageMap.computeIfAbsent(roomId, k -> new ArrayList<>()).add(imagePath);
                }
            }
        }

        // ✅ roomDtoList 순회하며 이미지 병합
        for (int i = 0; i < roomDtoList.size(); i++) {
            PlaceRoomDto roomDto = roomDtoList.get(i);
            List<String> finalRoomImages = new ArrayList<>();
            
            // 기존 이미지
            if (existingRoomImageMap.containsKey(i)) {
                finalRoomImages.addAll(existingRoomImageMap.get(i));
            }

            // 신규 업로드 이미지
            if (roomImageMapByIndex.containsKey(i)) {
                List<MultipartFile> newFiles = roomImageMapByIndex.get(i);
                List<String> saved = fileService.save(newFiles);
                finalRoomImages.addAll(saved);
            }

            // img1 ~ img10 필드 설정
            for (int j = 0; j < finalRoomImages.size(); j++) {
                Field field = PlaceRoomDto.class.getDeclaredField("img" + (j + 1));
                field.setAccessible(true);
                field.set(roomDto, finalRoomImages.get(j));
            }

        }

        // 3. 최종 저장
        placeService.updatePlace(placeId, placeDto, roomDtoList, sharedTimeSlots);
        return ResponseEntity.ok("장소 수정 완료");
    }
}
