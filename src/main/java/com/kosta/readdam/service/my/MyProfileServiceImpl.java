package com.kosta.readdam.service.my;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;

@Service
public class MyProfileServiceImpl implements MyProfileService {

	@Autowired
    private UserRepository userRepository;
    
    @Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDto getMyProfile(String username) throws Exception{
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserDto dto = user.toDto();
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            dto.setPassword("exists"); 
        }

        return dto;
    }
    
    @Override
    @Transactional
    public void updateMyProfile(UserDto dto) throws Exception{
        User user = userRepository.findById(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 값이 있을 때만 반영
        if (notBlank(dto.getName())) user.setName(dto.getName());
        if (notBlank(dto.getNickname())) user.setNickname(dto.getNickname());
        if (notBlank(dto.getPhone())) user.setPhone(dto.getPhone());
        if (notBlank(dto.getEmail())) user.setEmail(dto.getEmail());
        if (dto.getBirth() != null) user.setBirth(dto.getBirth());
        if (notBlank(dto.getIntroduce())) user.setIntroduce(dto.getIntroduce());
        if (notBlank(dto.getProfileImg())) user.setProfileImg(dto.getProfileImg());
        if (dto.getLat() != null) user.setLat(dto.getLat());
        if (dto.getLng() != null) user.setLng(dto.getLng());

        if (notBlank(dto.getPassword())) {
            String encoded = bCryptPasswordEncoder.encode(dto.getPassword());
            user.setPassword(encoded);
        }

        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void withdrawUser(String username, String reason) throws Exception {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        user.setDeleted(true);
        user.setWithdrawalReason(reason);
        user.setWithdrawalDate(LocalDateTime.now());

        userRepository.save(user);
    }

    private boolean notBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    
    
    
}
