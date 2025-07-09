package com.kosta.readdam.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kosta.readdam.jwt.JwtProperties;
import com.kosta.readdam.jwt.JwtToken;

@Service
public class TokenService {
	
	@Autowired
	private JwtToken jwtToken;
	
	 public String refreshAccessToken(String refreshToken) {
	        // 1) JWT 서명·만료(exp)만 검사
	        try {
	            Algorithm alg = Algorithm.HMAC512(JwtProperties.SECRET);
	            JWT.require(alg).build().verify(refreshToken);
	        } catch (Exception ex) {
	            throw new RuntimeException("유효하지 않거나 만료된 리프레시 토큰입니다.");
	        }
	        // 2) 페이로드에서 username만 뽑아내서 새 액세스 토큰 생성
	        String username = JWT.decode(refreshToken).getSubject();
	        return jwtToken.makeAccessToken(username, /*nickname*/"", /*isAdmin*/false);
	    }

}
