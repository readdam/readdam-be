package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.repository.AlertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyAlertServiceImpl implements MyAlertService {

    private final AlertRepository alertRepository;

    /* 전체 목록 (페이징 없이) */
    @Override
    public List<AlertDto> getMyAlerts(String receiverUsername) {
        return alertRepository
                .findByReceiverUsernameOrderByAlertIdDesc(receiverUsername)
                .stream()
                .map(Alert::toDto)
                .collect(Collectors.toList());
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

