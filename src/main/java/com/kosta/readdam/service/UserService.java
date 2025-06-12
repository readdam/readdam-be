package com.kosta.readdam.service;

import com.kosta.readdam.dto.UserDto;

public interface UserService {
	
	UserDto login(String email,String password) throws Exception;

	void join(UserDto userDto) throws Exception;

}
