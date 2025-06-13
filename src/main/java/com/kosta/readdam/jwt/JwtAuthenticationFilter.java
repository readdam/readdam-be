package com.kosta.readdam.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.entity.User;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	private JwtToken jwtToken = new JwtToken();

	//super의 attemptAuthentication 메소드가 실행되고 성공하면 successfulAuthentication가 호출된다.
	//attemptAuthentication 메소드가 리턴해준 Authentication을 파라미터로 받아옴
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
	        Authentication authResult) throws IOException, ServletException {

	    PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
	    User user = principalDetails.getUser();

	    String username = user.getUsername();
	    String nickname = user.getNickname();
	    Boolean isAdmin = user.getIsAdmin();
	    Double lat = user.getLat();
	    Double lng = user.getLng();

	    String accessToken = jwtToken.makeAccessToken(username, nickname, isAdmin, lat, lng);
	    String refreshToken = jwtToken.makeRefreshToken(username);

	    // 헤더에는 access token만 포함
	    response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);

	    // 응답 바디에 accessToken 내용에 해당하는 정보 포함
	    Map<String, Object> responseBody = new HashMap<>();
	    responseBody.put("username", username);
	    responseBody.put("nickname", nickname);
	    responseBody.put("isAdmin", isAdmin);
	    responseBody.put("lat", lat);
	    responseBody.put("lng", lng);
	    responseBody.put("refresh_token", JwtProperties.TOKEN_PREFIX + refreshToken);

	    response.setContentType("application/json; charset=utf-8");
	    ObjectMapper objectMapper = new ObjectMapper();
	    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
	}

}
