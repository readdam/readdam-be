package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

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
}
