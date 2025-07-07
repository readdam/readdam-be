package com.kosta.readdam.dto;

import java.time.LocalDateTime;

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
    private String title;
    private Boolean isChecked;
    private String senderUsername;
    private String senderNickname;
    private String type;
    private String imageUrl;
    private String linkUrl;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledTime;    

    public Alert toEntity(User sender, User receiver) {
        return Alert.builder()
            .alertId(alertId)
            .title(title)
            .content(content)
            .isChecked(isChecked != null ? isChecked : false)
            .sender(sender)
            .receiver(receiver)
            .type(type)
            .imageUrl(imageUrl)
            .linkUrl(linkUrl)
            .scheduledTime(scheduledTime)   
            .build();
    }
}
