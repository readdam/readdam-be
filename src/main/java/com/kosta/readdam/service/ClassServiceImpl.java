package com.kosta.readdam.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.ClassRepository;

@Service
public class ClassServiceImpl implements ClassService {

	@Autowired
	EntityManager entityManager;
	
	@Autowired
	ClassRepository classRepository;
	
	@Value("${iupload.path}")
	private String iuploadPath;
	
	
	private String saveImage(MultipartFile ifile) throws IOException {
	    if (ifile != null && !ifile.isEmpty()) {
	        String filename = UUID.randomUUID() + "_" + ifile.getOriginalFilename();
	        File dest = new File(iuploadPath, filename);
	        ifile.transferTo(dest);
	        return filename;
	    }
	    return null;
	}

	
	@Override
	@Transactional
	public Integer createClass(ClassDto classDto, Map<String, MultipartFile> imageMap, User leader) throws Exception {
		
		for(Map.Entry<String, MultipartFile> entry : imageMap.entrySet()) {
			String field = entry.getKey();	//"이미지이름"
			String savedFilename = saveImage(entry.getValue());
			
			if(savedFilename != null) {
				// 각 이미지 타입에 따라 DTO에 매핑
				switch(field) {
					case "mainImg" : classDto.setMainImg(savedFilename); break;
					case "leaderImg": classDto.setLeaderImg(savedFilename); break;
					case "round1Img": classDto.setRound1Img(savedFilename); break;
					case "round1Bookimg" : classDto.setRound1Bookimg(savedFilename); break;
					case "round2Img" : classDto.setRound2Img(savedFilename); break;
					case "round2Bookimg" : classDto.setRound2Bookimg(savedFilename); break;
					case "round3Img" : classDto.setRound3Img(savedFilename); break;
					case "round3Bookimg" : classDto.setRound3Bookimg(savedFilename); break;
					case "round4Img" : classDto.setRound4Img(savedFilename); break;
					case "round4Bookimg" : classDto.setRound4Bookimg(savedFilename); break;
				}
			}
		}
		
		ClassEntity cEntity = classDto.toEntity(leader);
		classRepository.save(cEntity);
		Integer classId = cEntity.getClassId();
		entityManager.clear();
		return classId;
	}

	@Override
	public ClassDto detailClass(Integer classId) throws Exception {
		ClassEntity cEntity = classRepository.findById(classId).orElseThrow(()->new Exception("글번호 오류"));
		System.out.println(cEntity.getClassIntro());
		return cEntity.toDto();
	}

}
