/**
 * 
 */
package com.kosta.readdam.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassQnaDto;
import com.kosta.readdam.entity.User;


public interface ClassService {
	Integer createClass(ClassDto classDto, Map<String, MultipartFile> imageMap, User leader) throws Exception;
	ClassDto detailClass(Integer classId) throws Exception;
	void createQna(ClassQnaDto classQnaDto, String username) throws Exception;
	List<ClassQnaDto> getQnaList(Integer classId) throws Exception;
}
