package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer>{
	
	 List<Inquiry> findByUser_UsernameOrderByRegDateDesc(String username);

}
