/**
 * 
 */
package com.kosta.readdam.service.klass;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.User;


public interface ClassService {
	Integer createClass(ClassDto classDto, Map<String, MultipartFile> imageMap, User leader) throws Exception;
	ClassDto detailClass(Integer classId) throws Exception;
	Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable);
	List<ClassDto> getLatestClasses() throws Exception; // homeClass 조회용
	List<ClassDto> searchForAll(String keyword, String sort, int limit) throws Exception; //통합검색용
}
