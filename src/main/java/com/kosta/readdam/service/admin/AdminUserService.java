package com.kosta.readdam.service.admin;


import java.util.List;

import com.kosta.readdam.dto.AdminUserDetailDto;
import com.kosta.readdam.dto.ClassUserDto;
import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.dto.UserSearchResponse;

public interface AdminUserService {

	UserSearchResponse getUserList(String keyword, int page, int size) throws Exception;

	AdminUserDetailDto getUserInfo(String username) throws Exception;

	List<ClassUserDto> getUserClassList(String username) throws Exception;

}
