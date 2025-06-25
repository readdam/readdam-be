package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.entity.ClassLike;
import com.kosta.readdam.repository.ClassLikeRepository;
import com.kosta.readdam.repository.ClassRepository;
import com.kosta.readdam.repository.ClassUserRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyClassLikeServiceImpl implements MyClassLikeService {

    private final ClassLikeRepository classLikeRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final ClassUserRepository classUserRepository;

    @Override
    public List<ClassDto> getLikedClasses(String username) throws Exception {
        List<ClassLike> likes = classLikeRepository.findAllByUserUsername(username);
        return likes.stream()
                .map(like -> {
                    ClassDto dto = like.getClassId().toDto();
                    dto.setLikeCount((int) classLikeRepository.countByClassIdClassId(like.getClassId().getClassId()));
                    dto.setLiked(true);
                    dto.setCurrentParticipants((int) classUserRepository.countByClassEntity_ClassId(like.getClassId().getClassId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String toggleLike(String username, Integer classId) throws Exception {
        Optional<ClassLike> existing = classLikeRepository.findByUserUsernameAndClassIdClassId(username, classId);
        if (existing.isPresent()) {
            classLikeRepository.delete(existing.get());
            return "좋아요 취소";
        } else {
            ClassLike newLike = ClassLike.builder()
                    .user(userRepository.findByUsername(username).orElseThrow())
                    .classId(classRepository.findById(classId).orElseThrow())
                    .date(LocalDateTime.now())
                    .build();
            classLikeRepository.save(newLike);
            return "좋아요 완료";
        }
    }
}
