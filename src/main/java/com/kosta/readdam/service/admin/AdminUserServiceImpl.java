package com.kosta.readdam.service.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.UserDto;
import com.kosta.readdam.dto.UserSearchResponse;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.util.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;
	
	@Override
	public UserSearchResponse getUserList(String keyword, int page, int size) throws Exception{
		if (keyword == null || keyword.trim().isEmpty()) {
			throw new IllegalArgumentException("검색어를 입력해주세요.");
		}
		
		Pageable pageable = PageRequest.of(
				Math.max(0, page - 1),
				Math.max(1, size),
				Sort.by(Sort.Direction.DESC, "username")
				);
		
		Page<User> users = userRepository
				.findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNameContainingIgnoreCase(
						keyword, keyword, keyword, keyword, pageable
						);
		
		List<UserDto> dtos = users.stream()
				.map(UserDto::fromEntity)
				.collect(Collectors.toList());
		
		Integer totalPages = users.getTotalPages();
		Integer totalElements = (int) users.getTotalElements();

		Integer startPage = Math.max(1, page - 2); // 1~5 페이징 범위 계산 등
		Integer endPage = Math.min(totalPages, page + 2);
		
		PageInfo pageInfo = new PageInfo(
			    page, totalPages, startPage, endPage
			);
		
		return new UserSearchResponse(dtos, pageInfo);
	}

}
