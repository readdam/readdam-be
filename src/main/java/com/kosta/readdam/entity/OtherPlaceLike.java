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

import com.kosta.readdam.dto.OtherPlaceLikeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "other_place_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherPlaceLike {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false, updatable = false)
    private Integer likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_place_id", nullable = false)
    private OtherPlace otherPlace;
    
    public OtherPlaceLikeDto toDto() {
    	return OtherPlaceLikeDto.builder()
    			.likeId(likeId)
                .username(user.getUsername())
                .otherPlaceId(otherPlace.getOtherPlaceId())
                .date(date)
                .build();
    }
}
