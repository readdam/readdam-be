package com.kosta.readdam.jwt;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtToken {
	public String makeAccessToken(String username, String nickname, Boolean isAdmin) {
	    return JWT.create()
	        .withSubject(username)
	        .withClaim("nickname", nickname)
	        .withClaim("isAdmin", isAdmin)
//	        .withClaim("lat", lat)
//	        .withClaim("lng", lng)
	        .withIssuedAt(new Date(System.currentTimeMillis()))
	        .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
	        .sign(Algorithm.HMAC512(JwtProperties.SECRET));
	}


	public String makeRefreshToken(String username) {
		return JWT.create()
				.withSubject(username)
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.REFRESH_EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
	}
}
