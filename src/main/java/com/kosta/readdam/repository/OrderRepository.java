package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Order;
import com.kosta.readdam.entity.enums.PaymentStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByOrderUuid(String orderUuid);
	
	
	 @Query("SELECT COALESCE(SUM(o.price),0) FROM Order o "
	         + "WHERE o.paymentStatus = :status "
	         + "  AND o.requestedAt BETWEEN :start AND :end")
	    Long sumPriceByStatusBetween(
	        @Param("status") PaymentStatus status,
	        @Param("start")  LocalDateTime start,
	        @Param("end")    LocalDateTime end
	    );

	    @Query("SELECT COUNT(o) FROM Order o "
	         + "WHERE o.paymentStatus = :status "
	         + "  AND o.requestedAt BETWEEN :start AND :end")
	    Long countByStatusBetween(
	        @Param("status") PaymentStatus status,
	        @Param("start")  LocalDateTime start,
	        @Param("end")    LocalDateTime end
	    );
}
