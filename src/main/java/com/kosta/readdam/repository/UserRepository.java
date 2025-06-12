package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.User;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByProviderAndProviderId(String provider, String providerId);

}
