package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceLike;
import com.kosta.readdam.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceLikeDto {

    private Integer likeId;
    private String username;
    private Integer placeId;
    private LocalDateTime date;

    public PlaceLike toEntity(User user, Place place) {
        return PlaceLike.builder()
                .likeId(likeId)
                .user(user)
                .place(place)
                .date(date != null ? date : LocalDateTime.now())
                .build();
    }
}
