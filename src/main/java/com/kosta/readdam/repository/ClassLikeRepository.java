package com.kosta.readdam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassLike;

public interface ClassLikeRepository extends JpaRepository<ClassLike, Integer> {

	boolean existsByUserUsernameAndClassIdClassId(String username, Integer classId);

	Optional<ClassLike> findByUserUsernameAndClassIdClassId(String username, Integer classId);

	List<ClassLike> findAllByUserUsername(String username);

	long countByClassIdClassId(Integer classId);
}
