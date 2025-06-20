package com.kosta.readdam.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	 List<String> save(List<MultipartFile> files);
}
