package com.kosta.readdam.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.kosta.readdam.dto.AlertDto;

import lombok.*;

@Entity
@Table(name = "alert")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Alert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id", updatable = false, nullable = false)
    private Integer alertId;

    @Column(length = 100, nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_username", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_username", nullable = false)
    private User receiver;

    @Column(length = 50)
    private String type;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    /** 예약 발송 시각. null 이면 즉시 발송용 */
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public AlertDto toDto() {
        return AlertDto.builder()
            .alertId(alertId)
            .title(title)
            .content(content)
            .isChecked(isChecked)
            .senderUsername(sender.getUsername())
            .senderNickname(sender.getNickname())
            .type(type)
            .imageUrl(imageUrl)
            .linkUrl(linkUrl)
            .createdAt(createdAt)
            .build();
    }
}
