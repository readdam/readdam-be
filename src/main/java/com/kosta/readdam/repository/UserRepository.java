package com.kosta.readdam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kosta.readdam.entity.User;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByProviderAndProviderId(String provider, String providerId);
	Optional<User> findByUsername(String username);
	List<User> findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(String username, String nickname);
	
	@Query("select u.username from User u")
	List<String> findAllUsernames();
	Page<User> findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNameContainingIgnoreCase(
			String keyword, String keyword2, String keyword3, String keyword4, Pageable pageable);

}
