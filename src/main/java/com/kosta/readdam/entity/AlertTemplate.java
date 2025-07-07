package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.kosta.readdam.dto.AlertTemplateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_template")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AlertTemplate {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id", updatable = false, nullable = false)
    private Long templateId;

    @Column(name = "code", length = 100, nullable = false, unique = true)
    private String code;

    @Column(name = "title_tpl", length = 255, nullable = false)
    private String titleTpl;

    @Column(name = "body_tpl", columnDefinition = "TEXT", nullable = false)
    private String bodyTpl;

    @Column(name = "default_image_url", length = 500)
    private String defaultImageUrl;

    @Column(name = "default_link_url", length = 500)
    private String defaultLinkUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    

    public AlertTemplateDto toDto() {
        return AlertTemplateDto.builder()
            .templateId(this.templateId)
            .code(this.code)
            .titleTpl(this.titleTpl)
            .bodyTpl(this.bodyTpl)
            .defaultImageUrl(this.defaultImageUrl)
            .defaultLinkUrl(this.defaultLinkUrl)
            .createdAt(this.createdAt)
            .build();
    }


}
