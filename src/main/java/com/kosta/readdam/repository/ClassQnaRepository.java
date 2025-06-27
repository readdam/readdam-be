package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassQna;

public interface ClassQnaRepository extends JpaRepository<ClassQna, Integer> {

	List<ClassQna> findByClassEntity_ClassIdOrderByRegDateDesc(Integer classId);

}
