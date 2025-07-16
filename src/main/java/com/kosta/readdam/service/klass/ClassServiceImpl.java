package com.kosta.readdam.service.klass;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

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
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassUser;
import com.kosta.readdam.entity.Point;
import com.kosta.readdam.entity.Reservation;
import com.kosta.readdam.entity.ReservationDetail;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.ClassStatus;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.PointRepository;
import com.kosta.readdam.repository.ReservationDetailRepository;
import com.kosta.readdam.repository.ReservationRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.repository.klass.ClassQnaRepository;
import com.kosta.readdam.repository.klass.ClassRepository;
import com.kosta.readdam.repository.klass.ClassRepositoryCustom;
import com.kosta.readdam.repository.klass.ClassUserRepository;
import com.kosta.readdam.repository.place.PlaceRoomRepository;
import com.kosta.readdam.repository.place.PlaceTimeRepository;
import com.kosta.readdam.repository.reservation.ReservationDslRepositoryCustom;
import com.kosta.readdam.service.alert.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

	private final EntityManager                   entityManager;
    private final ClassRepository                 classRepository;
    private final ClassQnaRepository              classQnaRepository;
    private final UserRepository                  userRepository;
    private final ClassRepositoryCustom           classRepositoryCustom;
    private final ReservationDslRepositoryCustom  rdRepositoryCustom;
    private final ReservationRepository           reservationRepository;
    private final ClassUserRepository             classUserRepository;
    private final PointRepository                 pointRepository;
    private final PlaceRoomRepository             placeRoomRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final PlaceTimeRepository placeTimeRepository;
	private final AlertRepository alertRepository;
	private final NotificationService notificationService;



    @Value("${iupload.path}")
    private String                          iuploadPath;

    private void mapImageToDto(ClassDto dto, String fieldName, String savedFilename) {
        try {
            String setterName = "set"
                + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
            Method setter = ClassDto.class.getMethod(setterName, String.class);
            setter.invoke(dto, savedFilename);
        } catch (Exception e) {
            System.err.println("이미지 매핑 실패: " + fieldName);
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public Integer createClass(
            ClassDto classDto,
            Map<String, MultipartFile> imageMap,
            User leader
    ) throws Exception {
        for (Map.Entry<String, MultipartFile> entry : imageMap.entrySet()) {
            String field = entry.getKey();
            MultipartFile file = entry.getValue();

            if (file != null && !file.isEmpty()) {
                String savedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File uploadDir = new File(iuploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File dest = new File(uploadDir, savedFilename);
                file.transferTo(dest);

                String cleanedField = field.endsWith("F")
                    ? field.substring(0, field.length() - 1)
                    : field;
                mapImageToDto(classDto, cleanedField, savedFilename);
            }
        }

        ClassEntity cEntity = classDto.toEntity(leader);
        classRepository.save(cEntity);  // classId 생성

        // 2) 예약 연결 (DB에 있는 Reservation만 꺼내서 업데이트)
        List<Integer> rids = classDto.getReservationIds();
        if (rids != null && !rids.isEmpty()) {
            List<Reservation> list = reservationRepository.findAllById(rids);
            if (list.size() != rids.size()) {
                throw new EntityNotFoundException("예약 일부를 찾을 수 없습니다.");
            }
            for (Reservation r : list) {
                r.setClassEntity(cEntity);
                reservationRepository.save(r);
            }
        }

        return cEntity.getClassId();
    }

    @Override
    public ClassDto detailClass(Integer classId) throws Exception {
        ClassEntity cEntity = classRepository.findById(classId)
            .orElseThrow(() -> new Exception("모임글번호 오류"));
        return cEntity.toDto();
    }

    @Override
    public Slice<ClassCardDto> searchClasses(
            ClassSearchConditionDto condition,
            Pageable pageable
    ) {
        return classRepository.searchClasses(condition, pageable);
    }

    @Override
    public List<ClassDto> getLatestClasses() throws Exception {
        return classRepository.findTop4ByOrderByClassIdDesc()
            .stream()
            .map(ClassEntity::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public SearchResultDto<ClassDto> searchForAll(
            String keyword,
            String sort,
            int limit
    ) throws Exception {
        return classRepositoryCustom.searchForAll(keyword, sort, limit);
    }

    @Override
    public List<PlaceReservInfoDto> getPlaceReservInfo(String username) throws Exception {
        return rdRepositoryCustom.findAllPlaceReservations(username);
    }

    @Override
    @Transactional
    public void cancelJoinClass(Integer classId, String username) {
        // 1) 참여 기록 조회
        ClassUser cu = classUserRepository
            .findByClassEntity_ClassIdAndUser_Username(classId, username)
            .orElseThrow(() -> new EntityNotFoundException("참여 기록이 없습니다."));

        // 2) ReservationDetail을 classId로 조회하여 detailCount 합산
        List<ReservationDetail> details =
            reservationDetailRepository.findByReservation_ClassEntity_ClassId(classId);
        int detailCount = details.size();

        // 3) 환불 포인트 계산 (join 때와 동일한 식)
        int minPerson = cu.getClassEntity().getMinPerson();
        int refund = detailCount > 0
            ? 500 * detailCount / minPerson
            : 0;

        // 4) 사용자 포인트 복원
        User u = cu.getUser();
        int currentPoint = u.getTotalPoint() != null ? u.getTotalPoint() : 0;
        u.setTotalPoint(currentPoint + refund);
        userRepository.save(u);

        // 5) 포인트 내역 저장
        pointRepository.save(Point.builder()
            .user(u)
            .point(refund)
            .reason("강의 참여 취소 환불 (강의ID: " + classId + ")")
            .build()
        );

       
        classUserRepository.delete(cu);
    }



    @Override
    @Transactional
    public void joinClass(Integer classId, String username) {
        // 1) 클래스 조회 및 상태 체크
        ClassEntity c = classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다. id=" + classId));
        if (c.getStatus() == ClassStatus.CANCELLED) {
            throw new IllegalStateException("취소된 강의는 신청할 수 없습니다.");
        }

        // 2) 중복 신청 방지
        if (classUserRepository.existsByClassEntityAndUserUsername(c, username)) {
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }

        // 3) 사용자 조회
        User u = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. username=" + username));

        // 4) 최소 인원 검증
        Integer minPerson = c.getMinPerson();
        if (minPerson == null || minPerson <= 0) {
            throw new IllegalStateException("최소 인원이 설정되지 않았습니다.");
        }

        // 5) 회차별 날짜 리스트 준비
        List<LocalDate> dates = Stream.of(
                c.getRound1Date(),
                c.getRound2Date(),
                c.getRound3Date(),
                c.getRound4Date()
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (dates.isEmpty()) {
            throw new IllegalStateException("모임 일정이 설정되지 않았습니다.");
        }

        // 6) 기존 Reservation(템플릿) 가져오기
        List<Reservation> templateReservations =
            reservationRepository.findByClassEntity_ClassId(classId);
        if (templateReservations.size() < dates.size()) {
            throw new IllegalStateException("회차 수에 맞는 예약 정보가 없습니다.");
        }

        // 7) 총 디테일 수 계산
        int totalDetails = templateReservations.stream()
            .mapToInt(res -> res.getDetails().size())
            .sum();

        // 8) 포인트 차감: 500 * totalDetails / minPerson
        int deduction = totalDetails > 0 ? 500 * totalDetails / minPerson : 0;
        int currentPoint = u.getTotalPoint() != null ? u.getTotalPoint() : 0;
        u.setTotalPoint(currentPoint - deduction);
        userRepository.save(u);
        pointRepository.save(Point.builder()
            .user(u)
            .point(-deduction)
            .reason("강의 참여 결제 (강의ID: " + classId + ")")
            .build()
        );

        // 9) ClassUser 기록
        classUserRepository.save(ClassUser.builder()
            .classEntity(c)
            .user(u)
            .joinDate(LocalDateTime.now())
            .build()
        );
        
     // 10) 모임장에게 알림 저장 & FCM 푸시
        User leader = c.getLeader();                     // 모임장 User 객체
        String leaderUsername = leader.getUsername();    // 모임장 아이디

        String alertType = "CLASS_JOIN";
        String title     = "모임 참여 알림";
        String content   = u.getUsername() + "님이 모임( " + c.getTitle() + ")에 참여했습니다.";
        String link      = "/myAlert";

        // 중복 방지
        if (!alertRepository.existsByReceiverUsernameAndTypeAndTitle(
                leaderUsername, alertType, title)) {

            Alert alert = Alert.builder()
                .sender(u)               // 참여자를 발신자로
                .receiver(leader)        // 모임장
                .type(alertType)
                .title(title)
                .content(content)
                .linkUrl(link)
                .build();
            alertRepository.save(alert);

            Map<String,String> data = new HashMap<>();
            data.put("type",    alertType);
            data.put("classId", classId.toString());
            data.put("linkUrl", link);

            notificationService.sendPush(
                leaderUsername,
                title,
                content,
                data
            );
        }


    }



}
