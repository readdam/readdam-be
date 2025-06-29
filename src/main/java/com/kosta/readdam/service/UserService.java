package com.kosta.readdam.service;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.UserDto;

public interface UserService {
	
	UserDto login(String email,String password) throws Exception;

	void join(UserDto userDto, MultipartFile file) throws Exception;

	UserDto updateLocation(String username, Double latitude, Double longitude) throws Exception;

	UserDto getUser(String username)throws Exception ;
}
