package com.kosta.readdam.service;

import java.util.List;

public interface BookLikeService {
	boolean toggleLike(String username, String bookIsbn);
	List<String> getLikedIsbns(String username, String query, String target, String sort, int page, int size);
}
