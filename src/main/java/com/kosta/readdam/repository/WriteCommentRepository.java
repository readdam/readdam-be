package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.WriteComment;

public interface WriteCommentRepository extends JpaRepository<WriteComment, Integer> {
	List<WriteComment> findByWrite_WriteId(Integer writeId); 
	long countByWriteWriteId(Integer writeId);
	List<WriteComment> findByUserUsernameOrderByRegDateDesc(String username);
	//boolean existsByWrite_WriteIdAndUser_Username(Integer writeId, String username); 
	boolean existsByWrite_WriteIdAndAdoptedTrue(Integer writeId);
	List<WriteComment> findByWrite_WriteIdAndIsHideFalse(Integer writeId); //하이드 조건 추가
	Long countByWrite_WriteIdAndIsHideFalse(Integer writeId); //하이드 조건 추가
	boolean existsByWriteCommentIdAndAdoptedTrue(Integer writeCommentId);
	long countByWrite_WriteIdAndUser_UsernameAndIsHideFalse(Integer writeId, String username);
}
