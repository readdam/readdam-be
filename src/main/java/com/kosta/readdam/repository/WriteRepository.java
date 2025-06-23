package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kosta.readdam.entity.Write;

public interface WriteRepository extends JpaRepository<Write, Integer>, WriteDslRepository {
	// Write 엔티티에서 최신순(regDate 기준)으로 데이터를 조회하는 쿼리 메서드
	@Query("SELECT w FROM Write w WHERE w.isHide = false ORDER BY w.regDate DESC")
	List<Write> findLatest(Pageable pageable);

	List<Write> findByUserUsernameOrderByRegDateDesc(String username);
}
