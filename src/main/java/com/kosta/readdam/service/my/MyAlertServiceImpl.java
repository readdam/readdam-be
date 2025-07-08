package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.util.PageInfo2;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyAlertServiceImpl implements MyAlertService {

    private final AlertRepository alertRepository;

    @Override
    public PagedResponse<AlertDto> getMyAlerts(String receiverUsername, int page, int size) {
        // 1) JPA 페이징 조회
        Page<Alert> alertPage = alertRepository
            .findByReceiverUsernameOrderByAlertIdDesc(
                receiverUsername,
                PageRequest.of(page, size)
            );

        // 2) DTO 변환
        List<AlertDto> dtos = alertPage.getContent().stream()
            .map(Alert::toDto)
            .collect(Collectors.toList());

        // 3) PageInfo2 생성 (static factory 사용)
        PageInfo2 pageInfo = PageInfo2.from(alertPage);

        // 4) PagedResponse 반환
        return new PagedResponse<>(dtos, pageInfo);
    }



    /* 알림 읽음 처리 */
    @Override
    @Transactional      // ← 쓰기 트랜잭션
    public void checkAlert(Integer alertId) throws Exception {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("해당 알림이 존재하지 않습니다."));
        alert.setChecked(true);   // 변경 감지 → 별도 save() 불필요
    }

    /* 미확인 개수 */
    @Override
    public long countUnread(String receiverUsername) {
        return alertRepository
                .countByReceiverUsernameAndIsCheckedFalse(receiverUsername);
    }

    /* 최신 N건 */
    @Override
    public List<AlertDto> getLatestAlerts(String receiverUsername, int limit) {
        return alertRepository
                .findByReceiverUsernameOrderByAlertIdDesc(
                        receiverUsername, PageRequest.of(0, limit))
                .stream()
                .map(Alert::toDto)
                .collect(Collectors.toList());
    }
}

