package com.kosta.readdam.service;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;

@Service
public class ClassServiceImpl implements ClassService {

	@Autowired
	EntityManager entityManager;
	
	@Value("${iupload.path}")
	private String iuploadPath;

	@Value("${dupload.path}")
	private String duploadPath;
	
	@Override
	public Integer createClass(ClassDto classDto, MultipartFile ifile, MultipartFile dfile) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassDto detailClass(Integer classId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
