package com.kosta.readdam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Order;
import com.kosta.readdam.entity.Point;

public interface PointRepository extends JpaRepository<Point, Integer>{
	List<Point> findByUser_UsernameOrderByDateDesc(String username);
	Optional<Point> findTopByOrderAndPointGreaterThanOrderByDateDesc(Order order, int point);
}
