package com.kosta.readdam.service.klass;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassQnaDto;
import com.kosta.readdam.dto.ClassReviewDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassQna;
import com.kosta.readdam.entity.ClassReview;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.ClassQnaRepository;
import com.kosta.readdam.repository.ClassRepository;
import com.kosta.readdam.repository.ClassReviewRepository;
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
	ClassReviewRepository classReviewRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Value("${iupload.path}")
	private String iuploadPath;
	
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
		Boolean isLeader = qna.getClassEntity().getLeader().getUsername().equals(username);
		
		// 관리자 여부 확인(UserEntity)
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new Exception("사용자 정보가 존재하지 않습니다."));
		boolean isAdmin = Boolean.TRUE.equals(user.getIsAdmin());

		if(!isLeader && !isAdmin) {
			throw new Exception("모임장 또는 관리자만 답변할 수 있습니다.");
		}
		
		qna.setAnswer(answer);
		qna.setRegDate(LocalDateTime.now());
		
		classQnaRepository.save(qna);
		
	}

	

	@Override
	@Transactional(readOnly = true)
	public List<ClassReviewDto> getReviewsByClassId(Integer classId) {
		List<ClassReview> reviewEntities = classReviewRepository.findByClassEntity_ClassId(classId);
		
		List<ClassReviewDto> reviewDtoList = reviewEntities.stream().map(reviews -> {
			ClassReviewDto rDto = ClassReviewDto.builder()
					.classId(classId)
					.classReviewId(reviews.getClassReviewId())
					.content(reviews.getContent())
					.img(reviews.getImg())
					.rating(reviews.getRating())
					.isHide(reviews.getIsHide())
					.regDate(reviews.getRegDate())
					.username(reviews.getUser().getUsername())
					.build();
			return rDto;
		}).collect(Collectors.toList());
		return reviewDtoList;
	}


	@Override
	@Transactional
	public void createReview(User user, Integer classId, String content, Integer rating, String img, MultipartFile ifile) throws Exception{
		ClassEntity classEntity = classRepository.findById(classId)
				.orElseThrow(()-> new Exception("해당 모임을 찾을 수 없습니다."));
		
//		user = userRepository.findById(user.getUsername())
//				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		
		if(ifile != null && !ifile.isEmpty()) {
			img = ifile.getOriginalFilename();
			File upFile = new File(iuploadPath, img);
			ifile.transferTo(upFile);
		}
		
		ClassReview review = ClassReview.builder()
				.classEntity(classEntity)
				.user(user)
				.content(content)
				.rating(rating)
				.img(img)
				.regDate(LocalDateTime.now())
				.isHide(false)
				.build();
		
		classReviewRepository.save(review);
		
	}




}
