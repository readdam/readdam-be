package com.kosta.readdam.service.my;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassUserDto;
import com.kosta.readdam.dto.PagedResponse;

public interface MyClassUserService {

	PagedResponse<ClassUserDto> getOngoingClasses(String username, int page, int size);

	PagedResponse<ClassUserDto> getPastClasses(String username, int page, int size);
	
    PagedResponse<ClassDto> getCreatedClasses(String username, int page, int size);

}
