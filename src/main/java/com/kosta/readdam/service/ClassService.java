/**
 * 
 */
package com.kosta.readdam.service;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;

/**
 * @author KOSTA
 *
 */

public interface ClassService {
	Integer createClass(ClassDto classDto, MultipartFile ifile, MultipartFile dfile) throws Exception;
	ClassDto detailClass(Integer classId) throws Exception;
}
