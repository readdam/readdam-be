package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;

public interface AlertRepository extends JpaRepository<Alert, Integer>{

	List<Alert> findByScheduledTimeLessThanEqual(LocalDateTime now);
	
	long countByReceiverAndIsCheckedFalse(User receiver);
	
	Optional<Alert> findTopByReceiverAndIsCheckedFalseOrderByCreatedAtDesc(User receiver);
	
	List<Alert> findByReceiverUsernameOrderByAlertIdDesc(String username);

    Page<Alert> findByReceiverUsernameOrderByAlertIdDesc(
            String username, Pageable pageable);

    long countByReceiverUsernameAndIsCheckedFalse(String username);
}
