package com.kosta.readdam.service;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;	
	
	@Value("${iupload.path}")
	private String iuploadPath;

	@Override
	public UserDto login(String username, String password) throws Exception {
		User user = userRepository.findById(username).orElseThrow(()->new Exception("아이디오류"));
		if(!user.getPassword().equals(password)) throw new Exception("비밀번호오류");
		return user.toDto();
	}
	
	@Override
	public void join(UserDto userDto, MultipartFile file) throws Exception {
	    // 1. 중복 체크
		Optional<User> omember = userRepository.findById(userDto.getUsername());
		if(omember.isPresent()) throw new Exception("아이디 중복오류");

	    // 2. 파일 업로드
	    String profileImgName = null;
	    if (file != null && !file.isEmpty()) {
	        String ext = file.getOriginalFilename()
	                         .substring(file.getOriginalFilename().lastIndexOf("."));
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
	}

}
