package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
	// 관리자용 //
	List<Notice> findAllByOrderByTopFixDescRegDateDesc();
	
	// 유저용 //
	// 고정공지만 가져오기
	List<Notice> findByTopFixTrueOrderByRegDateDesc();
	// 일반공지만 가져오기
	Page<Notice> findByTopFixFalseOrderByRegDateDesc(Pageable pageable);
	// 검색 - 고정/일반 둘 다 포함
	Page<Notice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByTopFixDescRegDateDesc(
	        String titleKeyword,
	        String contentKeyword,
	        Pageable pageable
	);
}
