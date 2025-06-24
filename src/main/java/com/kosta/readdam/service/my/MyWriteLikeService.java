package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.WriteDto;

public interface MyWriteLikeService {

	List<WriteDto> getLikedWrites(String username) throws Exception;

	String toggleLike(String username, Integer writeId) throws Exception;

}
