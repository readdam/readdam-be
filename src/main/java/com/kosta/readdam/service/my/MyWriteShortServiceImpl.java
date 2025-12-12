// src/main/java/com/kosta/readdam/service/my/MyWriteShortServiceImpl.java
package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.entity.WriteShortLike;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteShortLikeRepository;
import com.kosta.readdam.repository.WriteShortRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyWriteShortServiceImpl implements MyWriteShortService {

    private final WriteShortRepository writeShortRepository;
    private final WriteShortLikeRepository writeShortLikeRepository;
    private final UserRepository userRepository;

    /** 1. 내 글 전체 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<WriteShortDto> getMyWriteShorts(String username) throws Exception{
        User me = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return writeShortRepository
            .findByUser_UsernameAndIsHideFalseOrderByRegDateDesc(username)
            .stream()
            .map(ws -> {
                boolean isLiked = writeShortLikeRepository
                    .findByWriteShortAndUser(ws, me)
                    .isPresent();
                int likes = ws.getLikes();
                return WriteShortDto.from(ws, isLiked, likes);
            })
            .collect(Collectors.toList());
    }

    /** 2. 좋아요 토글 후 최신 DTO 반환 */
    @Override
    @Transactional
    public WriteShortDto toggleLike(Integer writeshortId, String username)throws Exception {
        WriteShort ws = writeShortRepository.findById(writeshortId)
            .orElseThrow(() -> new EntityNotFoundException("글을 찾을 수 없습니다."));
        User me = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        WriteShortLike existing = writeShortLikeRepository
            .findByWriteShortAndUser(ws, me)
            .orElse(null);

        if (existing != null) {
            // 삭제
            writeShortLikeRepository.delete(existing);
            ws.setLikes(ws.getLikes() - 1);
        } else {
            // 생성
            WriteShortLike newLike = WriteShortLike.builder()
                .writeShort(ws)
                .user(me)
                .date(LocalDateTime.now())  
                .build();
            writeShortLikeRepository.save(newLike);
            ws.setLikes(ws.getLikes() + 1);
        }

        // 최신 좋아요 수와 상태로 DTO 생성
        boolean isLikedNow = (existing == null);
        int likesNow = ws.getLikes();
        return WriteShortDto.from(ws, isLikedNow, likesNow);
    }
}
