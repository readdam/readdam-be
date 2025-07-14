package com.kosta.readdam.service.klass;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.dto.PlaceReservInfoDto;
import com.kosta.readdam.dto.SearchResultDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassUser;
import com.kosta.readdam.entity.Point;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ClassStatus;
import com.kosta.readdam.repository.PointRepository;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.klass.ClassQnaRepository;
import com.kosta.readdam.repository.klass.ClassRepository;
import com.kosta.readdam.repository.klass.ClassRepositoryCustom;
import com.kosta.readdam.repository.klass.ClassUserRepository;
import com.kosta.readdam.repository.reservation.ReservationDslRepositoryCustom;



@Service
public class ClassServiceImpl implements ClassService {

	@Autowired
	EntityManager entityManager;
	
	@Autowired
	ClassRepository classRepository;
	
	@Autowired
	ClassQnaRepository classQnaRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ClassRepositoryCustom classRepositoryCustom;
	
	@Autowired
	ReservationDslRepositoryCustom rdRepositoryCustom;
	
	@Autowired
	ReservationRepository reservationRepository;
	
	@Autowired
	ClassUserRepository classUserRepository;
	
	@Autowired
	PointRepository pointRepository;
	
	@Value("${iupload.path}")
	private String iuploadPath;
	

	private void mapImageToDto(ClassDto dto, String fieldName, String savedFilename) {
		try {
			String setterName = "set" + Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
			Method setter = ClassDto.class.getMethod(setterName, String.class);
			setter.invoke(dto, savedFilename);
		}catch (Exception e) {
			System.err.println("이미지매핑 실패: "+ fieldName);
			e.printStackTrace();
		}
	}
	
	@Override
	@Transactional
	public Integer createClass(ClassDto classDto, Map<String, MultipartFile> imageMap, User leader) throws Exception {
		
		for(Map.Entry<String, MultipartFile> entry : imageMap.entrySet()) {
			String field = entry.getKey();	//"이미지이름"
			MultipartFile file = entry.getValue();
			
			if (file != null && !file.isEmpty()) {
	            // 업로드할 파일명 생성
	            String savedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();

	            // 저장 경로 확보 및 생성
	            File uploadDir = new File(iuploadPath);
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }

	            // 파일 실제 저장
	            File dest = new File(uploadDir, savedFilename);
	            file.transferTo(dest);
	            
	         // F 접미사 제거해서 DTO 필드명에 매핑
	            String cleanedField = field.endsWith("F") ? field.substring(0, field.length() - 1) : field;
	            // DTO에 저장된 파일명 매핑
	            mapImageToDto(classDto, cleanedField, savedFilename);
	        }
		}
		
		ClassEntity cEntity = classDto.toEntity(leader);

	    // 2) reservationId가 DTO에 있으면 예약 연결
	    if (classDto.getReservationId() != null) {
	        Reservation r = reservationRepository.findById(classDto.getReservationId())
	            .orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다: " + classDto.getReservationId()));
	        cEntity.setReservation(r);
	    }
	    classRepository.save(cEntity);
	    entityManager.clear();
	    return cEntity.getClassId();
	}

	@Override
	public ClassDto detailClass(Integer classId) throws Exception {
		ClassEntity cEntity = classRepository.findById(classId).orElseThrow(()->new Exception("모임글번호 오류"));
		System.out.println(cEntity.getClassIntro());
		return cEntity.toDto();
	}

	@Override
	public Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable) {
		
		return classRepository.searchClasses(condition,pageable);
	}

	public List<ClassDto> getLatestClasses() throws Exception {
	    List<ClassEntity> classes = classRepository.findTop4ByOrderByClassIdDesc();
	    return classes.stream()
	            .map(ClassEntity::toDto)
	            .collect(Collectors.toList());
	}

	@Override
	public SearchResultDto<ClassDto> searchForAll(String keyword, String sort, int limit) throws Exception {
		return classRepositoryCustom.searchForAll(keyword, sort, limit);
	}

	@Override
	public List<PlaceReservInfoDto> getPlaceReservInfo(String username) throws Exception {
		return rdRepositoryCustom.findAllPlaceReservations(username);
	}
	
	@Override
    @Transactional
    public void cancelJoinClass(Integer classId, String username) {
        ClassEntity c = classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다. id=" + classId));

        // 확정된 강의는 취소 불가
        if (c.getStatus() == ClassStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 강의는 취소할 수 없습니다.");
        }

        ClassUser cu = classUserRepository
            .findByClassEntityAndUserUsername(c, username)
            .orElseThrow(() -> new EntityNotFoundException("참여 기록이 없습니다."));

        // 포인트 환불
        User u = cu.getUser();
        int refund = 500 * c.getTotalTime() / c.getMinPerson();
        u.setTotalPoint(u.getTotalPoint() + refund);
        userRepository.save(u);
        
        Point refundLog = Point.builder()
                .user(u)
                .point(refund)  // 환불이니까 양수
                .reason("강의 참여 취소 환불 (강의ID: " + classId + ")")
                .build();
            pointRepository.save(refundLog);

        classUserRepository.delete(cu);
    }
	
	@Override
	@Transactional
	public void joinClass(Integer classId, String username) {
	    ClassEntity c = classRepository.findById(classId)
	        .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다. id=" + classId));

	    // 1) 상태 검사
	    if (c.getStatus() == ClassStatus.CANCELLED) {
	        throw new IllegalStateException("취소된 강의는 신청할 수 없습니다.");
	    }
	    // 2) 중복 신청 방지
	    if (classUserRepository.existsByClassEntityAndUserUsername(c, username)) {
	        throw new IllegalStateException("이미 신청한 강의입니다.");
	    }

	    User u = userRepository.findByUsername(username)
	        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. username=" + username));

	    // 3) 최소 인원 체크
	    Integer minPerson = c.getMinPerson();
	    if (minPerson == null || minPerson <= 0) {
	        throw new IllegalStateException("최소 인원이 설정되지 않았습니다.");
	    }

	 // 4) 총 세션(시간) 계산
	    int totalTime = c.getTotalTime();
	    if (totalTime <= 0) {
	        throw new IllegalStateException("예약된 시간이 없습니다.");
	    }

	    // 5) 포인트 차감 계산
	    int deduction = 500 * totalTime / minPerson;  // <-- 여기서 deduction을 선언하고
	    int currentPoint = (u.getTotalPoint() != null) ? u.getTotalPoint() : 0;
	    
	    
	    
	    int newPoint     = currentPoint - deduction; // <-- 그 다음에 사용합니다

	    // 6) 포인트 차감 & 기록
	    u.setTotalPoint(newPoint);
	    userRepository.save(u);
	    pointRepository.save(Point.builder()
	        .user(u)
	        .point(-deduction)
	        .reason("강의 참여 결제 (강의ID: " + classId + ")")
	        .build()
	    );


	    // 6) 참여 정보 저장
	    ClassUser cu = ClassUser.builder()
	        .classEntity(c)
	        .user(u)
	        .joinDate(LocalDateTime.now())
	        .build();
	    classUserRepository.save(cu);
	}


	
}
