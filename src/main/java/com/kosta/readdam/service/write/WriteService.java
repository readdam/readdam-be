package com.kosta.readdam.service.write;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;

public interface WriteService {
	Integer writeDam(WriteDto writeDto, MultipartFile ifile, User user) throws Exception;
	WriteDto detailWrite(Integer writeId) throws Exception;
	List<WriteDto> findLatest(int limit);
	Page<Write> searchWrites(WriteSearchRequestDto cond, Pageable pageable);
}
