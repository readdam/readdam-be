package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.WriteShortLike;

public interface WriteShortLikeRepository extends JpaRepository<WriteShortLike, Long> {

	Optional<WriteShortLike> findByUserUsernameAndWriteShortWriteshortId(String username, Integer writeshortId);

	long countByWriteShortWriteshortId(Integer writeshortId);

}
