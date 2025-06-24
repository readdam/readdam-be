// com/kosta/readdam/dto/WriteShortLikeDto.java
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
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WriteShortLikeDto {

    private Long likeId;
    private Integer writeshortId;
    private String username;
    private LocalDateTime likedAt;

    
    public WriteShortLike toEntity(WriteShort writeShort, User user) {
        return WriteShortLike.builder()
            .likeId(this.likeId)
            .writeShort(writeShort)
            .user(user)
            .likedAt(this.likedAt != null ? this.likedAt : LocalDateTime.now())
            .build();
    }
}
