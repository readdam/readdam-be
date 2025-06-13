package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;

public interface AlertRepository extends JpaRepository<Alert, Integer>{

	 List<Alert> findByReceiverUsernameOrderByAlertIdDesc(String receiverUsername);

}
