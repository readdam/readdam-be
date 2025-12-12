package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.WriteShort;
import com.kosta.readdam.entity.WriteShortLike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WriteShortLikeDto {
    private Integer likeId;
    private String username;
    private Integer writeshortId;
    private LocalDateTime date;  // 좋아요 누른 시각

    /**
     * 엔티티 → DTO
     */
    public static WriteShortLikeDto from(WriteShortLike entity) {
        return WriteShortLikeDto.builder()
            .likeId(entity.getLikeId())
            .username(entity.getUser().getUsername())
            .writeshortId(entity.getWriteShort().getWriteshortId())
            .date(entity.getDate())
            .build();
    }

    public WriteShortLike toEntity(WriteShort writeShort, User user) {
        return WriteShortLike.builder()
            .likeId(this.likeId)
            .writeShort(writeShort)
            .user(user)
            .date(this.date != null ? this.date : LocalDateTime.now())
            .build();
    }
    

}
