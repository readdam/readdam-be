package com.kosta.readdam.repository.klass;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.ClassUser;

public interface ClassUserRepository extends JpaRepository<ClassUser, Long> {
	long countByClassEntity_ClassId(Integer classId);

	List<ClassUser> findByUser_Username(String username);

	Page<ClassUser> findByUser_UsernameAndClassEntity_EndDateGreaterThanEqualOrderByClassEntity_EndDateDesc(
			String username, LocalDate today, Pageable pageable);

	Page<ClassUser> findByUser_UsernameAndClassEntity_EndDateLessThanOrderByClassEntity_EndDateDesc(String username,
			LocalDate today, Pageable pageable);
}
