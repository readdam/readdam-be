package com.kosta.readdam.repository;

import java.util.List;

import com.kosta.readdam.entity.ClassEntity;

public interface BookClassRepository {

	List<ClassEntity> findByBookTitleAndAuthor(String title, String author);

}