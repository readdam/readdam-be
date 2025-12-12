package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceLike;
import com.kosta.readdam.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlaceLikeDto {
	
	private Integer likeId;
	private String username;
    private Integer otherPlaceId;
    private LocalDateTime date;
    
    public OtherPlaceLike toEntity(User user, OtherPlace otherPlace) {
    	return OtherPlaceLike.builder()
    			.likeId(likeId)
    			.user(user)
    			.otherPlace(otherPlace)
    			.date(date != null ? date : LocalDateTime.now())
                .build();
    }
}
