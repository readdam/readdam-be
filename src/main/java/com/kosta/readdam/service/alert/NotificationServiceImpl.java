package com.kosta.readdam.service.alert;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository    userRepository;
    private final AlertRepository   alertRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendPush(String receiverUsername,
                         String title,
                         String body,
                         Map<String, String> data) {

        User user = userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + receiverUsername));

        String token = user.getFcmToken();
        if (token == null || token.isBlank()) return;

        // [생략] 요약 모드 처리…

        // ── payload 준비 ──
        Map<String, String> payload = new HashMap<>();
        if (data != null) {
            payload.putAll(data);
        }

        // 기본 아이콘·뱃지·타임스탬프
        payload.putIfAbsent("iconUrl",  "https://example.com/favicon-192.png");
        payload.putIfAbsent("badgeUrl", "https://example.com/badge-72.png");
        payload.putIfAbsent("timestamp", String.valueOf(System.currentTimeMillis()));

        // ★ imageUrl 조건부 추가
        String img = (data != null ? data.get("imageUrl") : null);
        if (img != null && !img.isBlank()) {
            payload.put("imageUrl", img);
        }

        // ── 메시지 빌드 & 전송 ──
        Message msg = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(payload)
            .build();

        try {
            firebaseMessaging.send(msg);
            log.info("FCM 전송 성공: {}", receiverUsername);
        } catch (FirebaseMessagingException ex) {
            log.error("FCM 전송 실패 code={}", ex.getMessagingErrorCode(), ex);
        }
    }
}

