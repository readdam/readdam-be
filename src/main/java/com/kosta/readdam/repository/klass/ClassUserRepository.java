package com.kosta.readdam.repository.klass;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassUser;

public interface ClassUserRepository extends JpaRepository<ClassUser, Long> {
	long countByClassEntity_ClassId(Integer classId);

	List<ClassUser> findByUser_Username(String username);

	Page<ClassUser> findByUser_UsernameAndClassEntity_EndDateGreaterThanEqualOrderByClassEntity_EndDateDesc(
			String username, LocalDate today, Pageable pageable);

	Page<ClassUser> findByUser_UsernameAndClassEntity_EndDateLessThanOrderByClassEntity_EndDateDesc(String username,
			LocalDate today, Pageable pageable);

	long countByClassEntityAndLeftDateIsNull(ClassEntity c);

	Optional<ClassUser> findByClassEntityAndUserUsername(ClassEntity c, String username);

	boolean existsByClassEntityAndUserUsername(ClassEntity c, String username);

	boolean existsByClassEntity_ClassIdAndUser_Username(Integer classId, String username);

	boolean existsByClassEntityAndUser_Username(ClassEntity c, String username);
	
	long countByClassEntity_ClassIdAndLeftDateIsNull(Integer classId);
}
