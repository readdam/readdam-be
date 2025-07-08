package com.kosta.readdam.dto;

import java.util.List;

import com.kosta.readdam.util.PageInfo;


import lombok.Data;

@Data
public class UserSearchResponse {
	private List<UserDto> users;
	private PageInfo pageInfo;
	
	public UserSearchResponse(List<UserDto> users, PageInfo pageInfo) {
        this.users = users;
        this.pageInfo = pageInfo;
    }

}
