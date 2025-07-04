package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.dto.NoticeDto;
import com.kosta.readdam.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

	List<Notice> findAllByOrderByTopFixDescRegDateDesc();

}
