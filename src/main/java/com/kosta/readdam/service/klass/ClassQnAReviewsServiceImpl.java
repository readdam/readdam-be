package com.kosta.readdam.service.klass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.ClassQnaDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassQna;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.ClassQnaRepository;
import com.kosta.readdam.repository.ClassRepository;
import com.kosta.readdam.repository.UserRepository;

@Service
public class ClassQnAReviewsServiceImpl implements ClassQnAReviewsService {

	@Autowired
	EntityManager entityManager;
	
	@Autowired
	ClassRepository classRepository;
	
	@Autowired
	ClassQnaRepository classQnaRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	@Transactional
	public void createQna(ClassQnaDto classQnaDto, String username) throws Exception {
		ClassEntity cEntity = classRepository.findById(classQnaDto.getClassId())
				.orElseThrow(() -> new Exception("해당 모임이 존재하지 않습니다."));
		
		User user = userRepository.findById(username)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		
		ClassQna qna = ClassQna.builder()
				.classEntity(cEntity)
				.user(user)
				.content(classQnaDto.getContent())
				.isSecret(classQnaDto.getIsSecret())
				.isHide(false)
				.regDate(LocalDateTime.now())
				.build();
		
		classQnaRepository.save(qna);
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<ClassQnaDto> getQnaList(Integer classId) throws Exception {
		List<ClassQna> qnaEntities = classQnaRepository.findByClassEntity_ClassIdOrderByRegDateDesc(classId);
		
		List<ClassQnaDto> dtoList = qnaEntities.stream().map(qna -> {
			ClassQnaDto dto = ClassQnaDto.builder()
					.classQnaId(qna.getClassQnaId())
					.classId(classId)
					.content(qna.getContent())
					.isSecret(qna.getIsSecret())
					.answer(qna.getAnswer())
					.regDate(qna.getRegDate())
					.username(qna.getUser().getUsername())
					.build();
			return dto;
		}).collect(Collectors.toList());
		return dtoList;
	}

	
	@Override
	@Transactional
	public void answerQna(Integer classQnaId, String answer, String username) throws Exception {
		ClassQna qna = classQnaRepository.findById(classQnaId)
				.orElseThrow(()->new Exception("질문이 존재하지 않습니다."));
		
		//모임장 여부 확인
		if (!qna.getClassEntity().getLeader().getUsername().equals(username)) {
			throw new Exception("모임장만 답변할 수 있습니다.");
		}
		
		qna.setAnswer(answer);
		qna.setRegDate(LocalDateTime.now());
		
		classQnaRepository.save(qna);
		
	}

}
