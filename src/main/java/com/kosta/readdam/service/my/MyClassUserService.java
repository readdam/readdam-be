package com.kosta.readdam.service.my;

import java.util.List;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassUserDto;

public interface MyClassUserService {

	List<ClassUserDto> getOngoingClasses(String username);

	List<ClassUserDto> getPastClasses(String username);

	List<ClassDto> getCreatedClasses(String username);

}
