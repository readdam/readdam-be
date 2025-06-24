package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.kosta.readdam.dto.WriteShortLikeDto;
import lombok.*;

@Entity
@Table(name = "write_short_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteShortLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", updatable = false, nullable = false)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writeshort_id", nullable = false)
    private WriteShort writeShort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;


    public WriteShortLikeDto toDto() {
        return WriteShortLikeDto.builder()
            .likeId(this.likeId)
            .writeshortId(this.writeShort.getWriteshortId())
            .username(this.user.getUsername())
            .likedAt(this.likedAt)
            .build();
    }
}
