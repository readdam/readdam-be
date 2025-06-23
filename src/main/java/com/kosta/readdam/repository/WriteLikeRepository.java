package com.kosta.readdam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kosta.readdam.entity.WriteLike;

public interface WriteLikeRepository extends JpaRepository<WriteLike, Integer> {

    long countByWriteWriteId(Integer writeId);
}
