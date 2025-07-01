package com.kosta.readdam.service.klass;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassQnaDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassQna;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.ClassQnaRepository;
import com.kosta.readdam.repository.ClassRepository;
import com.kosta.readdam.repository.UserRepository;

@Service
public class ClassServiceImpl implements ClassService {

	@Autowired
	EntityManager entityManager;
	
	@Autowired
	ClassRepository classRepository;
	
	@Autowired
	ClassQnaRepository classQnaRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Value("${iupload.path}")
	private String iuploadPath;
	

	private void mapImageToDto(ClassDto dto, String fieldName, String savedFilename) {
		try {
			String setterName = "set" + Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
			Method setter = ClassDto.class.getMethod(setterName, String.class);
			setter.invoke(dto, savedFilename);
		}catch (Exception e) {
			System.err.println("이미지매핑 실패: "+ fieldName);
			e.printStackTrace();
		}
	}
	
	@Override
	@Transactional
	public Integer createClass(ClassDto classDto, Map<String, MultipartFile> imageMap, User leader) throws Exception {
		
		for(Map.Entry<String, MultipartFile> entry : imageMap.entrySet()) {
			String field = entry.getKey();	//"이미지이름"
			MultipartFile file = entry.getValue();
			
			if (file != null && !file.isEmpty()) {
	            // 업로드할 파일명 생성
	            String savedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();

	            // 저장 경로 확보 및 생성
	            File uploadDir = new File(iuploadPath);
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }

	            // 파일 실제 저장
	            File dest = new File(uploadDir, savedFilename);
	            file.transferTo(dest);
	            
	         // F 접미사 제거해서 DTO 필드명에 매핑
	            String cleanedField = field.endsWith("F") ? field.substring(0, field.length() - 1) : field;
	            // DTO에 저장된 파일명 매핑
	            mapImageToDto(classDto, cleanedField, savedFilename);
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
		ClassEntity cEntity = classRepository.findById(classId).orElseThrow(()->new Exception("모임글번호 오류"));
		System.out.println(cEntity.getClassIntro());
		return cEntity.toDto();
	}


}
