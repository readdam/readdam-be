package com.kosta.readdam.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.dto.WriteShortLikeDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.entity.WriteShortLike;

public interface WriteShortLikeRepository extends JpaRepository<WriteShortLike, Integer> {
	
	boolean existsByUserUsernameAndWriteShortWriteshortId(String username, Integer writeShortId);
	void deleteByUserUsernameAndWriteShortWriteshortId(String username, Integer writeShortId);
	int countByWriteShort(WriteShort writeShort); //기본값 0이라 int로 함
	Collection<WriteShortLikeDto> findByWriteShort(WriteShort ws);
	Optional<WriteShortLike> findByWriteShortAndUser(WriteShort writeShort, User user);


}
