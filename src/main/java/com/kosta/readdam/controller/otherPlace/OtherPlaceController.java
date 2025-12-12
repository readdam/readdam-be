package com.kosta.readdam.controller.otherPlace;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.service.otherPlace.OtherPlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/otherPlace")
@RequiredArgsConstructor
public class OtherPlaceController {
	private final OtherPlaceService otherPlaceService;

    @GetMapping("/{id}")
    public ResponseEntity<OtherPlaceDto> getOtherPlaceDetail(@PathVariable Integer id) {
        OtherPlaceDto dto = otherPlaceService.getOtherPlaceDetail(id);
        return ResponseEntity.ok(dto);
    }
}
