package com.kosta.readdam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kosta.readdam.dto.AlertDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alert_id", updatable = false, nullable = false)
	private Integer alertId;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(name = "is_checked", nullable = false)
	private boolean isChecked;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_username", nullable = false)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_username", nullable = false)
	private User receiver;

	@Column(name = "type", length = 50)
	private String type;

	public AlertDto toDto() {
		return AlertDto.builder()
				.alertId(alertId)
				.content(content)
				.isChecked(isChecked)
				.senderUsername(sender.getUsername())
				.senderNickname(sender.getNickname())
				.type(type)
				.build();
	}
}
