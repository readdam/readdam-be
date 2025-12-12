package com.kosta.readdam.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.service.alert.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAlertServiceImpl implements AdminAlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public void sendCustomAlerts(
            String senderUsername,
            List<String> receiverUsernames,
            String type,
            String title,
            String content,
            String linkUrl,
            LocalDateTime scheduledTime,
            String imageName,
            boolean sendToAll
    ) {
        // 전체 발송이면 모든 username 조회
        if (sendToAll) {
            receiverUsernames = userRepository.findAllUsernames();
        }

        // 발신자 조회
        User sender = userRepository.findByUsername(senderUsername)
            .orElseThrow(() -> new IllegalArgumentException("발신자 사용자 없음: " + senderUsername));

        for (String username : receiverUsernames) {
            User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + username));

            Alert alert = Alert.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .title(title)
                .content(content)
                .linkUrl(linkUrl)
                .imageUrl(imageName)
                .isChecked(false)
                .scheduledTime(scheduledTime)
                .build();

            alertRepository.save(alert);

            // 즉시 발송
            if (scheduledTime == null || scheduledTime.isBefore(LocalDateTime.now())) {
                notificationService.sendPush(
                    receiver.getUsername(),
                    title,
                    content,
                    Map.of("type", type, "linkUrl", linkUrl != null ? linkUrl : "")
                );
            }
        }
    }
}
