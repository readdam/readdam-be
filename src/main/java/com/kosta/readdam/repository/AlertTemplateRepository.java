package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.AlertTemplate;

public interface AlertTemplateRepository extends JpaRepository<AlertTemplate, Long>{

	 Optional<AlertTemplate> findByCode(String code);

}
