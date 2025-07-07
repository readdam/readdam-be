package com.kosta.readdam.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class AlertTemplateDto {
    
    private Long templateId;

    private String code;

    private String titleTpl;

    private String bodyTpl;

    private String defaultImageUrl;

    private String defaultLinkUrl;
    
    private LocalDateTime createdAt;
    
    public Alert toEntity(User sender, User receiver, Map<String,String> params) {
        String content = bodyTpl;
        for (var e : params.entrySet()) {
            content = content.replace("{{" + e.getKey() + "}}", e.getValue());
        }

        return Alert.builder()
            .sender(sender)
            .receiver(receiver)
            .type(code)
            .content(content)
            .imageUrl(defaultImageUrl)
            .linkUrl(defaultLinkUrl)
            .isChecked(false)
            .build();
    }
}
