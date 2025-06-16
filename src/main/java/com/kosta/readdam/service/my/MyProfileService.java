package com.kosta.readdam.service.my;

import com.kosta.readdam.dto.UserDto;

public interface MyProfileService {

	UserDto getMyProfile(String username) throws Exception;

	void updateMyProfile(UserDto dto) throws Exception;

	void withdrawUser(String username, String reason) throws Exception;

}
