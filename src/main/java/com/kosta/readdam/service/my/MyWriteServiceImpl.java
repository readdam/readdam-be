package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteLikeRepository;
import com.kosta.readdam.repository.WriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyWriteServiceImpl implements MyWriteService{

    private final WriteRepository writeRepository;
    private final WriteLikeRepository writeLikeRepository;
    private final WriteCommentRepository writeCommentRepository;

    public List<WriteDto> getMyWrites(String username) throws Exception {
        List<Write> writes = writeRepository.findByUserUsernameOrderByRegDateDesc(username);

        return writes.stream().map(write -> {
            long likeCnt = writeLikeRepository.countByWriteWriteId(write.getWriteId());
            long commentCnt = writeCommentRepository.countByWriteWriteId(write.getWriteId());

            return write.toDto().toBuilder()
                    .likeCnt((int) likeCnt)
                    .commentCnt((int) commentCnt)
                    .build();
        }).collect(Collectors.toList());
    }
}