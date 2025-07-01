package com.kosta.readdam.service.klass;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassQnaDto;
import com.kosta.readdam.dto.ClassReviewDto;
import com.kosta.readdam.entity.User;

public interface ClassQnAReviewsService {
	void createQna(ClassQnaDto classQnaDto, String username) throws Exception;
	List<ClassQnaDto> getQnaList(Integer classId) throws Exception;
	void answerQna(Integer classQnaId, String answer, String username) throws Exception;
	List<ClassReviewDto> getReviewsByClassId(Integer classId);
	void createReview(User user, Integer classId, String content, Integer rating, String img, MultipartFile ifile) throws Exception;
	

}
