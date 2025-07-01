package com.kosta.readdam.service.write;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.WriteShortDto;
import com.kosta.readdam.entity.Event;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.entity.WriteShortLike;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.WriteShortLikeRepository;
import com.kosta.readdam.repository.WriteShortRepository;
import com.kosta.readdam.util.PageInfo2;

@Service
public class WriteShortServiceImpl implements WriteShortService {
	@Autowired
	private WriteShortRepository writeShortRepository;

	@Autowired
	private WriteShortLikeRepository writeShortLikeRepository;

	@Autowired
	private EventService eventService;

	@Autowired
	private UserRepository userRepository;

	@Override
	public WriteShortDto writePostcard(WriteShortDto writeShortDto, User user) {
		// 존재하는 Event인지 확인
		Event event = eventService.findCurrentEventEntity(LocalDateTime.now());
		// DTO의 toEntity() 메서드로 엔티티 생성 후 저장
		WriteShort saved = writeShortRepository.save(writeShortDto.toEntity(user, event));
		// 저장된 엔티티를 DTO로 변환하여 반환
		return WriteShortDto.from(saved);
	}

	@Override
	public Map<String, Object> getWriteShortListByCurrentEvent(int page, int size, String username) {
		// 1. 현재 이벤트 조회
		Event event = eventService.findCurrentEventEntity(LocalDateTime.now());
		// 2. 페이징 설정
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("regDate").descending());
		// 3. 해당 이벤트의 공개된 글 목록 조회
		Page<WriteShort> pageResult = writeShortRepository.findByEvent_EventIdAndIsHideFalse(event.getEventId(),
				pageable);
		// 4. 각 WriteShort → DTO 변환 (likes, isLiked 포함)
		List<WriteShortDto> list = pageResult.getContent().stream().map(writeShort -> {
			int likes = writeShortLikeRepository.countByWriteShort(writeShort); // 좋아요 수
			boolean isLiked = false;

			if (username != null) {
				isLiked = writeShortLikeRepository.existsByUserUsernameAndWriteShortWriteshortId(username,
						writeShort.getWriteshortId());
			}

			return WriteShortDto.from(writeShort, isLiked, likes);
		}).collect(Collectors.toList());

		// 5. 페이징 정보 구성
		PageInfo2 pageInfo = new PageInfo2(page, size, pageResult.isLast(), pageResult.getTotalElements(),
				pageResult.getTotalPages(), pageResult.hasNext());

		// 6. map으로 결과 구성
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("pageInfo", pageInfo);
		map.put("totalCount", pageResult.getTotalElements());
		return map;
	}

	@Override
	@Transactional // 삭제와 저장 로직에서 DB 반영을 보장하기 위해 사용
	public boolean toggleLike(String username, Integer writeshortId) {
		// 글 존재 확인
		WriteShort writeShort = writeShortRepository.findById(writeshortId)
				.orElseThrow(() -> new IllegalArgumentException("글 없음"));

		boolean exists = writeShortLikeRepository.existsByUserUsernameAndWriteShortWriteshortId(username, writeshortId);

		if (!exists) {
			// 좋아요 등록
			User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

			WriteShortLike newLike = WriteShortLike.builder().user(user).writeShort(writeShort)
					.date(LocalDateTime.now()).build();

			writeShortLikeRepository.save(newLike);

			// write_short.likes +1
			writeShort.setLikes(writeShort.getLikes() + 1);
			writeShortRepository.save(writeShort);

			return true;
		} else {
			// 좋아요 취소
			writeShortLikeRepository.deleteByUserUsernameAndWriteShortWriteshortId(username, writeshortId);

			// write_short.likes -1
			writeShort.setLikes(Math.max(0, writeShort.getLikes() - 1));
			writeShortRepository.save(writeShort);

			return false;
		}
	}

	// HomeShort 표출용 서비스
	@Override
	public List<WriteShortDto> findLatest(int limit, String username) throws Exception {
	    Event event = eventService.findCurrentEventEntity(LocalDateTime.now());
	    Pageable pageable = PageRequest.of(0, limit, Sort.by("regDate").descending());

	    List<WriteShort> writeShorts = writeShortRepository
	            .findByEvent_EventIdAndIsHideFalse(event.getEventId(), pageable)
	            .getContent();

	    return writeShorts.stream()
	            .map(writeShort -> {
	                int likes = writeShortLikeRepository.countByWriteShort(writeShort);
	                boolean isLiked = false;
	                if (username != null) {
	                    isLiked = writeShortLikeRepository
	                        .existsByUserUsernameAndWriteShortWriteshortId(username, writeShort.getWriteshortId());
	                }
	                return WriteShortDto.from(writeShort, isLiked, likes);
	            })
	            .collect(Collectors.toList());
	}
}
