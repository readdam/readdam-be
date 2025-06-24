package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.entity.WriteLike;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteLikeRepository;
import com.kosta.readdam.repository.WriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MyWriteLikeServiceImpl implements MyWriteLikeService{
	
	private final WriteLikeRepository writeLikeRepository;
    private final WriteRepository writeRepository;
    private final UserRepository userRepository;
    private final WriteCommentRepository writeCommentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WriteDto> getLikedWrites(String username) throws Exception {
        return writeLikeRepository.findAllByUserUsername(username).stream()
            .map(wl -> {
                Write write = wl.getWrite();
                int likeCnt = (int) writeLikeRepository.countByWriteWriteId(write.getWriteId());
                int commentCnt = (int) writeCommentRepository.countByWriteWriteId(write.getWriteId());
                return write.toDto().toBuilder()
                        .likeCnt(likeCnt)
                        .commentCnt(commentCnt)
                        .build();
            })
            .collect(Collectors.toList());
    }


    @Override
    public String toggleLike(String username, Integer writeId) throws Exception {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다. username=" + username));

        Write write = writeRepository.findById(writeId)
            .orElseThrow(() -> new IllegalArgumentException("글이 없습니다. writeId=" + writeId));

        return writeLikeRepository
            .findByUserUsernameAndWriteWriteId(username, writeId)
            .map(existing -> {
                writeLikeRepository.delete(existing);
                return "좋아요 취소";
            })
            .orElseGet(() -> {
                WriteLike newLike = WriteLike.builder()
                    .user(user)
                    .write(write)
                    .date(LocalDateTime.now())
                    .build();
                writeLikeRepository.save(newLike);
                return "좋아요 완료";
            });
    }

}
