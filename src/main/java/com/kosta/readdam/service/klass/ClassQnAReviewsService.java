package com.kosta.readdam.service;

import java.util.List;

import com.kosta.readdam.dto.ClassQnaDto;

public interface ClassQnAReviewsService {
	void createQna(ClassQnaDto classQnaDto, String username) throws Exception;
	List<ClassQnaDto> getQnaList(Integer classId) throws Exception;
	void answerQna(Integer classQnaId, String answer, String username) throws Exception;

}
