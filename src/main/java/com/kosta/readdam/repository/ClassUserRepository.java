package com.kosta.readdam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.ClassUser;

public interface ClassUserRepository extends JpaRepository<ClassUser, Long> {
    long countByClassEntity_ClassId(Integer classId);
    
    List<ClassUser> findByUser_Username(String username);
}
