package com.kosta.readdam.service.write;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.SearchResultDto;
import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.repository.WriteCommentRepository;
import com.kosta.readdam.repository.WriteDslRepository;
import com.kosta.readdam.repository.WriteLikeRepository;
import com.kosta.readdam.repository.WriteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WriteServiceImpl implements WriteService {
	@Autowired
	EntityManager entityManager; // 영속성 컨텍스트 초기화용 (clear)

	private final WriteRepository writeRepository;

	private final WriteLikeRepository writeLikeRepository;
	
	private final WriteCommentRepository writeCommentRepository;
	
	private final WriteDslRepository writeDslRepository;
	

	@Value("${iupload.path}")
	private String iuploadPath;
	
	private String getUniqueFileName(String originalName) {
	    String saveName = originalName;
	    File upFile = new File(iuploadPath, saveName);

	    int count = 1;
	    String namePart = originalName;
	    String extension = "";

	    int dotIndex = originalName.lastIndexOf(".");
	    if (dotIndex != -1) {
	        namePart = originalName.substring(0, dotIndex);
	        extension = originalName.substring(dotIndex);
	    }

	    while (upFile.exists()) {
	        saveName = namePart + "_" + count + extension;
	        upFile = new File(iuploadPath, saveName);
	        count++;
	    }

	    return saveName;
	}

	@Override
	@Transactional
	public Integer writeDam(WriteDto writeDto, MultipartFile ifile, User user) throws Exception {
		if (ifile != null && !ifile.isEmpty()) {
			
			String originalName = ifile.getOriginalFilename();
			String saveName = getUniqueFileName(originalName);
			File upFile = new File(iuploadPath, saveName);
			ifile.transferTo(upFile);

			writeDto.setImg(saveName);

	    } 
	    // 업로드 파일이 없고, 북커버 URL이 넘어온 경우
	    else if (writeDto.getThumbnailUrl() != null && !writeDto.getThumbnailUrl().isBlank()) {
	        // 북커버 URL을 그대로 img 컬럼에 저장
	        writeDto.setImg(writeDto.getThumbnailUrl());
	    } 
	    // 아무 이미지도 없는 경우
	    else {
	        writeDto.setImg(null);
	    }
		
		Write write = writeDto.toEntity(user);
		writeRepository.save(write);
		Integer writeId = write.getWriteId();
		entityManager.clear();
		return writeId;
	}

	@Override
	public WriteDto detailWrite(Integer writeId) throws Exception {
		System.out.println(writeId);
		Write write = writeRepository.findById(writeId).orElseThrow(() -> new Exception("글번호 오류"));
		WriteDto dto = write.toDto();
		return dto;
	}

	@Override
	public List<WriteDto> findLatest(int limit) throws Exception {
		// 1. 페이징 객체 생성: 0번째 페이지부터 limit 개수만큼 조회
		Pageable pageable = PageRequest.of(0, limit);

		// 2. 최신순 정렬된 글 데이터를 Repository에서 조회
		List<Write> writes = writeRepository.findLatest(pageable);

		
		// 3. Write 엔티티 리스트를 WriteDto 리스트로 변환 (스트림 map 사용)
	    return writes.stream().map(write -> {
	        WriteDto dto = write.toDto();
	        dto.setCommentCnt(write.getCommentCnt());
	        return dto;
	    }).collect(Collectors.toList());
	}

	@Override
	public Page<Write> searchWrites(WriteSearchRequestDto requestDto, Pageable pageable) {
		return writeDslRepository.searchWrites(requestDto, pageable);
	}

	@Override
	public boolean isLiked(String username, Integer writeId) throws Exception {
		return writeLikeRepository.findByUserUsernameAndWriteWriteId(username, writeId).isPresent();
	}

	@Override
	public int getLikeCount(Integer writeId) throws Exception {
			return (int) writeLikeRepository.countByWriteWriteId(writeId);
	}

	@Override
	public void increaseViewCount(Integer writeId) throws Exception {
		writeRepository.increaseViewCount(writeId);

	}

	@Override
	@Transactional
	public void modifyDam(WriteDto writeDto, MultipartFile ifile, User user) throws Exception {
		
		// 기존 글 가져오기
		Write write = writeRepository.findById(writeDto.getWriteId()).orElseThrow(()->new Exception("글번호오류"));
		
	    long commentCnt = writeCommentRepository.countByWrite_WriteIdAndIsHideFalse(write.getWriteId());

	    // 댓글 있으면 비공개 전환 불가
	    if ("private".equals(writeDto.getVisibility()) && commentCnt > 0) {
	        throw new IllegalStateException("댓글이 달린 글은 비공개로 전환할 수 없습니다.");
	    }

	    // 댓글 있으면 첨삭 해제 불가
	    boolean wasNeedReview = write.getEndDate() != null;
	    boolean wantsToCancelReview = wasNeedReview && !writeDto.isNeedReview();
	    
	    if (wantsToCancelReview && commentCnt > 0) {
	        throw new IllegalStateException("댓글이 달린 글은 첨삭 여부를 해제할 수 없습니다.");
	    }
	    
		write.setTitle(writeDto.getTitle());
		write.setContent(writeDto.getContent());
	    write.setType(writeDto.getWriteType());
	    write.setTag1(writeDto.getTag1());
	    write.setTag2(writeDto.getTag2());
	    write.setTag3(writeDto.getTag3());
	    write.setTag4(writeDto.getTag4());
	    write.setTag5(writeDto.getTag5());
	    write.setHide("private".equals(writeDto.getVisibility())); //isHide Lombok이 Hide로 맵핑함 참고
	    
	    // 빈 문자열은 null 처리
	    if (writeDto.getEndDate() != null && writeDto.getEndDate().toString().isBlank()) {
	        writeDto.setEndDate(null);
	    }
	 
	    // 첨삭 마감일 처리
	    if (writeDto.isNeedReview()) {
	        if (write.getEndDate() != null
	                && write.getEndDate().isBefore(LocalDateTime.now())) {
	            if (writeDto.getEndDate() != null
	                    && !writeDto.getEndDate().isEqual(write.getEndDate())) {
	                throw new IllegalStateException("첨삭 마감일이 이미 지난 글은 마감일을 수정할 수 없습니다.");
	            }
	        } else {
	            // 마감일 안 지났으면 자유롭게 수정 가능
	        	write.setEndDate(writeDto.getEndDate());
	        }
	    } else {
	    	// 첨삭을 원하지 않는다면 endDate를 null로
	        write.setEndDate(null);
	    }
	    
	    // 이미지 업로드 처리
		if (writeDto.getThumbnailUrl() != null && !writeDto.getThumbnailUrl().isBlank()) {
		    write.setImg(writeDto.getThumbnailUrl());
		} else if (ifile != null && !ifile.isEmpty()) {
			String originalName = ifile.getOriginalFilename();
			String saveName = getUniqueFileName(originalName);
			File upFile = new File(iuploadPath, saveName);
			ifile.transferTo(upFile);

			writeDto.setImg(saveName);
			write.setImg(saveName);
		}

		writeRepository.save(write);
	}

	@Override
	public SearchResultDto<WriteDto> searchForAll(String keyword, String sort, int limit) throws Exception {
		return writeDslRepository.searchForAll(keyword, sort, limit);
	}
}



