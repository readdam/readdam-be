package com.kosta.readdam.entity;

import javax.persistence.*;

import lombok.*;

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
    private Long alertId;

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
}
