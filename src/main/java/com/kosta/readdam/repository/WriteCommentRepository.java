package com.kosta.readdam.repository;

import java.util.List;

import com.kosta.readdam.entity.WriteComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteCommentRepository extends JpaRepository<WriteComment, Integer> {
	List<WriteComment> findByWrite_WriteId(Integer writeId); 
	long countByWriteWriteId(Integer writeId);
}
