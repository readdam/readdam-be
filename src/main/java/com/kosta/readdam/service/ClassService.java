/**
 * 
 */
package com.kosta.readdam.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.entity.User;


public interface ClassService {
	Integer createClass(ClassDto classDto, Map<String, MultipartFile> imageMap, User leader) throws Exception;
	ClassDto detailClass(Integer classId) throws Exception;
}
