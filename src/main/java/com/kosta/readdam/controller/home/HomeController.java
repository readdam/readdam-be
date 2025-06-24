package com.kosta.readdam.controller.home;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.service.write.WriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/") 
@RequiredArgsConstructor
public class HomeController {
	
	private final WriteService writeService;
	
    // ✅ 글쓰기 최신순 4개 가져오기
    @GetMapping("/writes")
    public List<WriteDto> getLatestWrites(@RequestParam(defaultValue = "4") int limit) {
        return writeService.findLatest(limit);
    }

	
	
	
}
