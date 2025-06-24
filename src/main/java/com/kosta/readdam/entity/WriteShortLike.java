package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    private Integer likeId;

//    private Long likeId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writeshort_id", nullable = false)
//    private WriteShort writeShort;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;


    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_short_id", nullable = false)
    private WriteShort writeShort;
    
    

//    @Column(name = "liked_at", nullable = false)
//    private LocalDateTime likedAt;


//    public WriteShortLikeDto toDto() {
//        return WriteShortLikeDto.builder()
//            .likeId(this.likeId)
//            .writeshortId(this.writeShort.getWriteshortId())
//            .username(this.user.getUsername())
//            .likedAt(this.likedAt)
//            .build();
//    }

}
