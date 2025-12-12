package com.kosta.readdam.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.service.alert.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	private final AlertRepository alertRepository;           
    private final NotificationService notificationService;

	@Value("${iupload.path}")
	private String iuploadPath;

	@Override
	public UserDto login(String username, String password) throws Exception {
		User user = userRepository.findById(username).orElseThrow(() -> new Exception("아이디오류"));
		if (!user.getPassword().equals(password))
			throw new Exception("비밀번호오류");
		return user.toDto();
	}

	@Override
	@Transactional
	public void join(UserDto userDto, MultipartFile file) throws Exception {
		// 1. 중복 체크
		Optional<User> omember = userRepository.findById(userDto.getUsername());
		if (omember.isPresent())
			throw new Exception("아이디 중복오류");

		// 2. 파일 업로드
		String profileImgName = null;
		if (file != null && !file.isEmpty()) {
			String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			profileImgName = UUID.randomUUID().toString() + ext;
			File saveFile = new File(iuploadPath, profileImgName);
			file.transferTo(saveFile);
		}

		// 3. DTO에 이미지 세팅
		userDto.setProfileImg(profileImgName);

		// 4. password 암호화
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

		// 5. 저장
		User user = userDto.toEntity();
		userRepository.save(user);
		
		User system = userRepository.findByUsername("system")
		        .orElseThrow(() -> new IllegalStateException("시스템 사용자(system)가 없습니다."));

		    // 6-2) 알림 메시지 정의
		    String title   = "환영합니다!";
		    String body    = user.getUsername() + "님, 회원가입을 축하드립니다 🎉";
		    String type = "welcome";

		    // 6-3) Alert 엔티티에 sender 필드 포함해서 생성
		    Alert alert = Alert.builder()
		        .sender(system)       // ★ 반드시 채워야 함
		        .receiver(user)
		        .title(title)
		        .content(body)
		        .type(type)
		        .build();
		    alertRepository.save(alert);

		    // 6-4) FCM 푸시
		    Map<String, String> data = new HashMap<>();
		    data.put("type", type);
		    notificationService.sendPush(
		        user.getUsername(),
		        title,
		        body,
		        data
		    );
		
		
	}

	@Override
	@Transactional
	public UserDto updateLocation(String username, Double latitude, Double longitude) throws Exception {
		User user = userRepository.findById(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

		user.setLat(latitude);
		user.setLng(longitude);

		return user.toDto();
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUser(String username) throws Exception {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

		return user.toDto();
	}

	@Override
	public List<UserDto> search(String q) {
		if (q == null || q.isBlank()) {
			return List.of();
		}
		return userRepository.findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(q, q).stream()
				.map(u -> UserDto.builder().username(u.getUsername()).nickname(u.getNickname()).build())
				.collect(Collectors.toList());
	}

	@Override
	public void updateFcmToken(String username, String token) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

		// 2) 토큰 갱신(또는 제거)
		if (token == null || token.isBlank()) {
			user.setFcmToken(null); // 토큰 삭제
		} else if (!token.equals(user.getFcmToken())) {
			user.setFcmToken(token); // 새 토큰 저장
		}
	}

}
