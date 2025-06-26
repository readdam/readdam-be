package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.Write;

public interface WriteRepository extends JpaRepository<Write, Integer>, WriteDslRepository {
	// Write 엔티티에서 최신순(regDate 기준)으로 데이터를 조회하는 쿼리 메서드
	@Query("SELECT w FROM Write w WHERE w.isHide = false ORDER BY w.regDate DESC")
	List<Write> findLatest(Pageable pageable);

	List<Write> findByUserUsernameOrderByRegDateDesc(String username);
	
    @Modifying //update 쿼리 명시
    @Transactional 
    @Query("UPDATE Write w SET w.viewCnt = w.viewCnt + 1 WHERE w.writeId = :writeId")
    void increaseViewCount(@Param("writeId") Integer writeId);
    
    @Modifying
    @Transactional 
    @Query("UPDATE Write w SET w.commentCnt = w.commentCnt + :countChange WHERE w.writeId = :writeId")
    void updateCommentCnt(@Param("writeId") Integer writeId, @Param("countChange") int countChange);
    
    
}
