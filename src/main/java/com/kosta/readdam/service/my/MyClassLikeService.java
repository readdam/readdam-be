package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.ClassDto;

public interface MyClassLikeService {

	List<ClassDto> getLikedClasses(String username) throws Exception;
    String toggleLike(String username, Integer classId) throws Exception;
}
