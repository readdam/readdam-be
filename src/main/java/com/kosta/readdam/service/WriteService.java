package com.kosta.readdam.service;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.entity.User;

public interface WriteService {
	Integer writeDam(WriteDto writeDto, MultipartFile ifile, User user) throws Exception;
	WriteDto detailWrite(Integer writeId) throws Exception;
}
