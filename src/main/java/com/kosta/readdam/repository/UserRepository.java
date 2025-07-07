package com.kosta.readdam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kosta.readdam.entity.User;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByProviderAndProviderId(String provider, String providerId);
	Optional<User> findByUsername(String username);
	List<User> findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(String username, String nickname);
	
	@Query("select u.username from User u")
	List<String> findAllUsernames();

}
