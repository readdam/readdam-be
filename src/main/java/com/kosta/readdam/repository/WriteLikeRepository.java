package com.kosta.readdam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.WriteLike;

public interface WriteLikeRepository extends JpaRepository<WriteLike, Integer> {

    long countByWriteWriteId(Integer writeId);
    
    Optional<WriteLike> findByUserUsernameAndWriteWriteId(String username, Integer writeId);
    List<WriteLike> findAllByUserUsername(String username);
}
