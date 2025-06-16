package com.kosta.readdam.service;

public interface BookLikeService {
	boolean toggleLike(String username, String bookIsbn);
}
