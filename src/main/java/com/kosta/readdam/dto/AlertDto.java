package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDto {

	private Integer alertId;
	private String content;
	private Boolean isChecked;
	private String senderUsername;
	private String senderNickname;
	private String type;

	public Alert toEntity(User sender, User receiver) {
		return Alert.builder()
				.alertId(alertId)
				.content(content)
				.isChecked(isChecked)
				.sender(sender)
				.receiver(receiver)
				.type(type)
				.build();
	}
}
