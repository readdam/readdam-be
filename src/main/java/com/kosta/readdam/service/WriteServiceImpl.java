package com.kosta.readdam.service;

import java.io.File;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.repository.WriteRepository;
@Service
public class WriteServiceImpl implements WriteService {
	@Autowired
	EntityManager entityManager; // 영속성 컨텍스트 초기화용 (clear)
	
	@Autowired
	private WriteRepository writeRepository;
	
	@Value("${iupload.path}")
	private String iuploadPath;
	
	@Override
	@Transactional
	public Integer writeDam(WriteDto writeDto, MultipartFile ifile, User user) throws Exception {
		if(ifile!=null && !ifile.isEmpty()) {
			writeDto.setImg(ifile.getOriginalFilename());
			File upFile = new File(iuploadPath,writeDto.getImg());
			ifile.transferTo(upFile);
		}
		
		Write write = writeDto.toEntity(user);
		writeRepository.save(write);
		Integer writeId = write.getWriteId();
		entityManager.clear();
		return writeId;
	}

	@Override
	public WriteDto detailWrite(Integer writeId) throws Exception {
		System.out.println(writeId);
		Write write = writeRepository.findById(writeId).orElseThrow(()->new Exception("글번호 오류"));
		return write.toDto();
	}

}
