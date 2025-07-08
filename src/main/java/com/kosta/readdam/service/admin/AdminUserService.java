package com.kosta.readdam.service.admin;


import com.kosta.readdam.dto.UserSearchResponse;

public interface AdminUserService {

	UserSearchResponse getUserList(String keyword, int page, int size) throws Exception;

}
