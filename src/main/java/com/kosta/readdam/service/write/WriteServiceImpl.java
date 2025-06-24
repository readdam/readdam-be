package com.kosta.readdam.service.write;

import java.io.File;
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

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.Write;
import com.kosta.readdam.repository.WriteRepository;
@Service
public class WriteServiceImpl implements WriteService {
	@Autowired
	EntityManager entityManager; // 영속성 컨텍스트 초기화용 (clear)
	
	@Autowired
	private WriteRepository writeRepository;
	
	@Value("${iupload.path}")
	private String iuploadPath;
	
	@Override
	@Transactional
	public Integer writeDam(WriteDto writeDto, MultipartFile ifile, User user) throws Exception {
		if(ifile!=null && !ifile.isEmpty()) {
			writeDto.setImg(ifile.getOriginalFilename());
			File upFile = new File(iuploadPath,writeDto.getImg());
			ifile.transferTo(upFile);
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
		Write write = writeRepository.findById(writeId).orElseThrow(()->new Exception("글번호 오류"));
		return write.toDto();
	}

	@Override
	public List<WriteDto> findLatest(int limit) {
	    // 1. 페이징 객체 생성: 0번째 페이지부터 limit 개수만큼 조회
	    Pageable pageable = PageRequest.of(0, limit);

	    // 2. 최신순 정렬된 글 데이터를 Repository에서 조회
	    List<Write> writes = writeRepository.findLatest(pageable);

	    // 3. Write 엔티티 리스트를 WriteDto 리스트로 변환 (스트림 map 사용)
	    return writes.stream()
	                 .map(WriteDto::from) // 각 Write → WriteDto 변환
	                 .collect(Collectors.toList()); // 변환 결과 리스트로 수집
	}

	@Override
	public Page<Write> searchWrites(WriteSearchRequestDto requestDto, Pageable pageable) {
		return writeRepository.searchWrites(requestDto, pageable);
	}


}
