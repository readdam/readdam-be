package com.kosta.readdam.service.alert;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.AlertRepository;
import com.kosta.readdam.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private final UserRepository userRepository;
	private final AlertRepository alertRepository;
	private final FirebaseMessaging firebaseMessaging;

	@Override
	public void sendPush(String receiverUsername, String title, String body, Map<String, String> data) {
		log.info("▶ sendPush 호출: title={} / body={}", title, body);

		User user = userRepository.findByUsername(receiverUsername).orElseThrow();
		String token = user.getFcmToken();
		if (token == null || token.isBlank())
			return;

		// 기본 data 페이로드
		Map<String, String> payload = new HashMap<>();
		if (data != null)
			payload.putAll(data);

		boolean isSummary = (title == null && body == null);
		if (isSummary) {
			// --- 요약 모드 (data-only) ---
			long unread = alertRepository.countByReceiverAndIsCheckedFalse(user);
			if (unread == 0)
				return;

			if (unread == 1) {
				Alert latest = alertRepository.findTopByReceiverAndIsCheckedFalseOrderByCreatedAtDesc(user)
						.orElseThrow();
				payload.put("title", latest.getTitle());
				payload.put("body", latest.getContent());
			} else {
				payload.put("title", "새로운 알림이 있습니다");
				payload.put("body", "읽지 않은 알림이 " + unread + "개 있습니다");
			}
			payload.put("url", "/myAlert");

			// data-only 메시지
			Message msg = Message.builder().setToken(token).putAllData(payload).build();
			sendMessage(msg, receiverUsername, "SUMMARY");

		} else {
			// --- 관리자가 보낸 일반 알림 (notification + data) ---
			payload.put("url", data != null ? data.get("linkUrl") : "/myAlert");

			// notification 페이로드 포함
			Message msg = Message.builder().setToken(token)
					.setNotification(Notification.builder().setTitle(title).setBody(body).build()).putAllData(payload)
					.build();
			sendMessage(msg, receiverUsername, "NORMAL");
		}
	}

	private void sendMessage(Message msg, String user, String type) {
		try {
			firebaseMessaging.send(msg);
			log.info("FCM 전송 성공 ({}): {}", type, user);
		} catch (FirebaseMessagingException ex) {
			log.error("FCM 전송 실패 code={}", ex.getMessagingErrorCode(), ex);
		}
	}
}
