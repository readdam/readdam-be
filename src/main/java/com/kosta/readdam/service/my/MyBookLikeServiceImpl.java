package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.BookLikeDto;
import com.kosta.readdam.entity.BookLike;
import com.kosta.readdam.repository.BookLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBookLikeServiceImpl implements MyBookLikeService {

	private final BookLikeRepository bookLikeRepository;

	@Override
    @Transactional(readOnly = true)
    public List<BookLikeDto> getLikedBooksByUsername(String username) throws Exception{
        List<BookLike> likes = bookLikeRepository.findByUser_Username(username);
        return likes.stream()
                .map(BookLike::toDto)
                .collect(Collectors.toList());
    }
	
}
