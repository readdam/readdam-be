package com.kosta.readdam.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kosta.readdam.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String username;
    private String name;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private LocalDate birth;
    private LocalDateTime joinDate;
    private String profileImg;
    private String fcmToken;
    private Boolean deleted;
    private String withdrawalReason;
    private LocalDateTime withdrawalDate;
    private Boolean isAdmin;
    private Double lat;
    private Double lng;
    private Integer totalPoint;
    private String introduce;
    
    public User toEntity() {
        return User.builder()
            .username(username)
            .password(password)
            .nickname(nickname)
            .phone(phone)
            .email(email)
            .birth(birth)
            .joinDate(joinDate)
            .profileImg(profileImg)
            .fcmToken(fcmToken)
            .deleted(deleted)
            .withdrawalReason(withdrawalReason)
            .withdrawalDate(withdrawalDate)
            .isAdmin(isAdmin)
            .lat(lat)
            .lng(lng)
            .totalPoint(totalPoint)
            .introduce(introduce)
            .build();
    }

}
