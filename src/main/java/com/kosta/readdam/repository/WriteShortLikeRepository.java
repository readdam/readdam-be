package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.entity.WriteShortLike;

public interface WriteShortLikeRepository extends JpaRepository<WriteShortLike, Integer> {
	//Optional<WriteShortLike> findByUserAndWriteShort(User user, WriteShort writeShort);
	boolean existsByUserUsernameAndWriteShortWriteshortId(String username, Integer writeShortId);
	void deleteByUserUsernameAndWriteShortWriteshortId(String username, Integer writeShortId);
	int countByWriteShort(WriteShort writeShort); //기본값 0이라 int로 함

}
