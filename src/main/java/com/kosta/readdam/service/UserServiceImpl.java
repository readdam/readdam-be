package com.kosta.readdam.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;	

	@Override
	public UserDto login(String username, String password) throws Exception {
		User user = userRepository.findById(username).orElseThrow(()->new Exception("아이디오류"));
		if(!user.getPassword().equals(password)) throw new Exception("비밀번호오류");
		return user.toDto();
	}
	
	@Override
	public void join(UserDto userDto) throws Exception {
		Optional<User> omember = userRepository.findById(userDto.getUsername());
		if(omember.isPresent()) throw new Exception("아이디 중복오류");
		userRepository.save(userDto.toEntity());
	}

}
