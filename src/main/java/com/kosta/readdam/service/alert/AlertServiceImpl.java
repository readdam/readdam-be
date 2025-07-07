package com.kosta.readdam.service.alert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.dto.AlertTemplateDto;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.AlertTemplate;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.AlertTemplateRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;                   // Alert 엔티티용
    private final AlertTemplateRepository templateRepository;       // AlertTemplate 엔티티용
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public List<AlertDto> getMyAlerts(String receiverUsername) {
        return alertRepository
            .findByReceiverUsernameOrderByAlertIdDesc(receiverUsername)
            .stream()
            .map(Alert::toDto)           // Alert → AlertDto
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkAlert(Integer alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 없습니다: " + alertId));
        alert.setChecked(true);
    }

    @Override
    public long countUnread(String receiverUsername) {
        return alertRepository.countByReceiverUsernameAndIsCheckedFalse(receiverUsername);
    }

    @Override
    @Transactional
    public void sendAlertFromTemplate(String templateCode,
                                      String receiverUsername,
                                      Map<String, String> params) {
        // 1) AlertTemplate 조회 — AlertTemplateRepository 사용!
        AlertTemplate template = templateRepository.findByCode(templateCode)
            .orElseThrow(() -> new IllegalArgumentException("템플릿이 없습니다: " + templateCode));

        // 2) DTO 변환
        AlertTemplateDto tplDto = AlertTemplateDto.builder()
            .templateId(template.getTemplateId())
            .code(template.getCode())
            .titleTpl(template.getTitleTpl())
            .bodyTpl(template.getBodyTpl())
            .defaultImageUrl(template.getDefaultImageUrl())
            .defaultLinkUrl(template.getDefaultLinkUrl())
            .createdAt(template.getCreatedAt())
            .build();

        // 3) 수신자 조회
        User receiver = userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다: " + receiverUsername));

        // 4) Alert 엔티티 생성 & 저장
        Alert alert = tplDto.toEntity(null, receiver, params);
        alertRepository.save(alert);

        // 5) 제목 치환 & FCM 푸시 전송
        String title = tplDto.getTitleTpl();
        for (var e : params.entrySet()) {
            title = title.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        notificationService.sendPush(
            receiverUsername,
            title,
            alert.getContent(),
            params
        );
    }

    @Override
    public void sendPointChargeAlert(String receiverUsername, int amount) {
        sendAlertFromTemplate(
            "POINT_CHARGE",
            receiverUsername,
            Map.of("amount", String.valueOf(amount))
        );
    }
}
