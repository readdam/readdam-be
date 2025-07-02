package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    List<ClassEntity> findTop4ByOrderByClassIdDesc(); // home 최신순 내림차순 + limit 4개 조회용 
}
