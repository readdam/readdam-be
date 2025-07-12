package com.kosta.readdam.service.klass;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassLike;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.klass.ClassLikeRepository;
import com.kosta.readdam.repository.klass.ClassRepository;

@Service
public class ClassLikeServiceImpl implements ClassLikeService {
	
	@Autowired
	EntityManager entityManager;
	
	@Autowired
	private ClassRepository classRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ClassLikeRepository classLikeRepository;
	
	@Override
	public Map<String, Object> getLikeStatus(Integer classId, String username) throws Exception {
		ClassEntity cEntity = classRepository.findById(classId)
				.orElseThrow(()-> new Exception("해당 모임이 존재하지 않습니다.")); 
		User user = userRepository.findByUsername(username)
				.orElseThrow(()-> new Exception("유저 정보가 없습니다."));
		
		Boolean liked = classLikeRepository.existsByUserUsernameAndClassIdClassId(username, classId);
		Integer LikeCount = classLikeRepository.countByClassIdClassId(classId);
		
		Map<String, Object> result = new HashMap<>();
		result.put("liked", liked);
		result.put("likeCount", LikeCount);
		return result;
	}

	@Override
	@Transactional
	public Map<String, Object> toggleLike(Integer classId, String username) throws Exception {
		ClassEntity cEntity = classRepository.findById(classId)
				.orElseThrow(()-> new Exception("해당 모임이 존재하지 않습니다.")); 
		User user = userRepository.findByUsername(username)
				.orElseThrow(()-> new Exception("유저 정보가 없습니다."));
		
		Optional<ClassLike> existing = classLikeRepository.findByUserUsernameAndClassIdClassId(username, classId);
		Boolean liked;
		
		if (existing.isPresent()) {
	        classLikeRepository.delete(existing.get());
	        liked = false;
	    } else {
	        ClassLike like = ClassLike.builder()
	                .classId(cEntity)
	                .user(user)
	                .date(LocalDateTime.now())
	                .build();
	        classLikeRepository.save(like);
	        liked = true;
	    }

	    Integer likeCount = classLikeRepository.countByClassIdClassId(classId);
	    Map<String, Object> result = new HashMap<>();
	    result.put("liked", liked);
	    result.put("likeCount", likeCount);
	    return result;
	}

}
