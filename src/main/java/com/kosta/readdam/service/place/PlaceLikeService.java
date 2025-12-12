package com.kosta.readdam.service.place;

public interface PlaceLikeService {
	boolean toggleLike(String username, Integer placeId);
    boolean isLiked(String username, Integer placeId);
    long countLikes(Integer placeId);
}
