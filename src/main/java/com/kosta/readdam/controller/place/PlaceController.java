package com.kosta.readdam.controller.place;

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

}
