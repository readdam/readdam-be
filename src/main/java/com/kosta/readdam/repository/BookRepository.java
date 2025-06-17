package com.kosta.readdam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Book;

public interface BookRepository extends JpaRepository<Book, String> {

}
