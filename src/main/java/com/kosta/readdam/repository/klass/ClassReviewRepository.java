package com.kosta.readdam.repository.klass;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassReview;

public interface ClassReviewRepository extends JpaRepository<ClassReview, Integer> {
	List<ClassReview> findByUser_Username(String username);
	List<ClassReview> findByClassEntity_ClassId(Integer classId);
}
