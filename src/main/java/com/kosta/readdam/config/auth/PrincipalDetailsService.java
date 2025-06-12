package com.kosta.readdam.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;

// security 설정에서 loginProcessingUrl("/loginProc")
// /loginProc 요청이 오면 자동으로 UserDetailsService의 타입으로 IoC 되어있는 loadUserByUsername 함수가 호출된다
@Service
public class PrincipalDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository; 

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println(username);
		User user = userRepository.findById(username).orElseThrow(()->new UsernameNotFoundException(username));
		System.out.println(user);
		return new PrincipalDetails(user);
	}
}
