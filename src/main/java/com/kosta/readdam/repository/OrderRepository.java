package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByOrderUuid(String orderUuid);
	
}
