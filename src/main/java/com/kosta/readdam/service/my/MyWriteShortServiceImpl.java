package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
public class MyWriteShortServiceImpl implements MyWriteShortService{

    private final WriteShortRepository writeShortRepository;
    private final WriteShortLikeRepository writeShortLikeRepository;
    private final UserRepository userRepository;

    @Override
    public List<WriteShortDto> getMyShorts(String username) throws Exception {
        return writeShortRepository.findDtosByUsername(username);
    }

    @Override
    public long toggleLike(String username, Integer writeshortId) throws Exception {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found: " + username));

        WriteShort ws = writeShortRepository.findById(writeshortId)
            .orElseThrow(() -> new Exception("WriteShort not found: " + writeshortId));

        Optional<WriteShortLike> existing = 
            writeShortLikeRepository.findByUserUsernameAndWriteShortWriteshortId(username, writeshortId);

        if (existing.isPresent()) {
            writeShortLikeRepository.delete(existing.get());
        } else {
            WriteShortLike like = WriteShortLike.builder()
                .writeShort(ws)
                .user(user)
                .likedAt(LocalDateTime.now())
                .build();
            writeShortLikeRepository.save(like);
        }

        return writeShortLikeRepository.countByWriteShortWriteshortId(writeshortId);
    }
    

}
