package com.kosta.readdam.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

import com.kosta.readdam.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	private String username;

	@Column
	private String name;
	
	@Column
	private String password;

	@Column
	private String nickname;

	@Column
	private String phone;

	@Column
	private String email;

	@Column
	private LocalDate birth;

	@Column(name = "join_date")
	private LocalDateTime joinDate;

	@Column(name = "profile_img")
	private String profileImg;

	@Column(name = "fcm_token")
	private String fcmToken;

	@Column(name = "deleted")
	private Boolean deleted;

	@Column(name = "withdrawal_reason")
	private String withdrawalReason;

	@Column(name = "withdrawal_date")
	private LocalDateTime withdrawalDate;

	@Column(name = "is_admin")
	@ColumnDefault("0") // 기본값설정 추가
	private Boolean isAdmin;

	@Column(name = "lat")
	private Double lat;

	@Column(name = "lng")
	private Double lng;

	@Column
	private String provider;

	@Column
	private String providerId;

	@Column
	private Integer totalPoint;
	
	@Column
	private String introduce;
	
	

	public UserDto toDto() {
		return UserDto.builder()
				.username(username)
				.password(null)
				.name(name)
				.nickname(nickname).phone(phone).email(email)
				.birth(birth).joinDate(joinDate).profileImg(profileImg).fcmToken(fcmToken).deleted(deleted)
				.withdrawalReason(withdrawalReason)
				.withdrawalDate(withdrawalDate)
				.isAdmin(isAdmin)
				.lat(lat)
				.lng(lng)
				.totalPoint(totalPoint != null ? totalPoint : 0)
				.introduce(introduce)
				.build();
	}

}
