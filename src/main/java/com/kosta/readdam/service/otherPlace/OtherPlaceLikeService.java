package com.kosta.readdam.service.otherPlace;

public interface OtherPlaceLikeService {
	boolean toggleLike(String username, Integer otherPlaceId);
    boolean isLiked(String username, Integer otherPlaceId);
    Integer countLikes(Integer otherPlaceId);
}
