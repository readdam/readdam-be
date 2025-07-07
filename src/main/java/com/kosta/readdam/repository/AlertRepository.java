package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;

public interface AlertRepository extends JpaRepository<Alert, Integer> {

	List<Alert> findByScheduledTimeLessThanEqual(LocalDateTime now);

	long countByReceiverAndIsCheckedFalse(User receiver);

	Optional<Alert> findTopByReceiverAndIsCheckedFalseOrderByCreatedAtDesc(User receiver);

	List<Alert> findByReceiverUsernameOrderByAlertIdDesc(String username);

	Page<Alert> findByReceiverUsernameOrderByAlertIdDesc(String username, Pageable pageable);

	long countByReceiverUsernameAndIsCheckedFalse(String username);

	@Modifying
	@Transactional
	@Query("DELETE FROM Alert a WHERE a.createdAt < :cutoff")
	int deleteByCreatedAtBefore(LocalDateTime cutoff);

	boolean existsByReceiverUsernameAndTypeAndTitle(String receiverUsername, String type, String title);
}
