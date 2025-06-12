package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "write_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", updatable = false, nullable = false)
    private Integer likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_id", nullable = false)
    private Write write;
}
