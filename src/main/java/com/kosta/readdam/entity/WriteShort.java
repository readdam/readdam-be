package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.kosta.readdam.dto.WriteShortDto;

import lombok.*;

@Entity
@Table(name = "write_short")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "writeshort_id", updatable = false, nullable = false)
    private Integer writeshortId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 20)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;
    
    @Column(name = "likes", nullable = false)
    @Builder.Default
    private Integer likes = 0;  // 좋아요 수 기본값 0으로 설정
    
    public static WriteShortDto from(WriteShort entity) {
        return WriteShortDto.builder()
            .writeshortId(entity.getWriteshortId())
            .content(entity.getContent())
            .color(entity.getColor())
            .regDate(entity.getRegDate())
            .username(entity.getUser().getUsername()) 
            .nickname(entity.getUser().getNickname()) 
            .likes(entity.getLikes())
            .build();
    }
}
