package com.kosta.readdam.repository;

import com.kosta.readdam.entity.ClassUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassUserRepository extends JpaRepository<ClassUser, Long> {
    long countByClassEntity_ClassId(Integer classId);
}
