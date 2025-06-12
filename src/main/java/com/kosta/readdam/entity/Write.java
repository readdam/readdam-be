package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "`write`") // write는 예약어라 백틱 필요
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Write {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "write_id", updatable = false, nullable = false)
    private Integer writeId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 50)
    private String tag1;

    @Column(length = 50)
    private String tag2;

    @Column(length = 50)
    private String tag3;

    @Column(length = 50)
    private String tag4;

    @Column(length = 50)
    private String tag5;

    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String img;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "view_cnt", nullable = false)
    private int viewCnt;

    @Column(name = "is_hide", nullable = false)
    private boolean isHide;
}
