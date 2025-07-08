package com.kosta.readdam.controller.my;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassUserDto;
import com.kosta.readdam.service.my.MyClassUserService;

@RestController
@RequestMapping("/my/classes")
public class MyClassController {
	
	private final MyClassUserService classUserService;

    @Autowired
    public MyClassController(MyClassUserService classUserService) {
        this.classUserService = classUserService;
    }

    /**
     * 진행 중인(참여 중) 모임 조회
     */
    @GetMapping("/ongoing")
    public List<ClassUserDto> getOngoingClasses(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return classUserService.getOngoingClasses(username);
    }

    /**
     * 과거에 참여했던(종료된) 모임 조회
     */
    @GetMapping("/past")
    public List<ClassUserDto> getPastClasses(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return classUserService.getPastClasses(username);
    }

    /**
     * 내가 만든 모든 모임 조회
     */
    @GetMapping("/created")
    public List<ClassDto> getCreatedClasses(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return classUserService.getCreatedClasses(username);
    }
    

}
