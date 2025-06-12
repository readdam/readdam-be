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
		PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();
		String username = principalDetails.getUsername();		
		
		User user = principalDetails.getUser();
		String nickname = user.getNickname();
		Boolean isAdmin = user.getIsAdmin();
		Double lat = user.getLat();
		Double lng = user.getLng();

		String accessToken = jwtToken.makeAccessToken(username, nickname, isAdmin, lat, lng);
		String refreshToken = jwtToken.makeRefreshToken(username);
		
		Map<String,String> map = new HashMap<>();
		map.put("access_token", JwtProperties.TOKEN_PREFIX+accessToken);
		map.put("refresh_token", JwtProperties.TOKEN_PREFIX+refreshToken);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String token = objectMapper.writeValueAsString(map);
		System.out.println(token);
		
		response.addHeader(JwtProperties.HEADER_STRING, token);
		response.setContentType("application/json; charset=utf-8");
		
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("username", user.getUsername());
		userInfo.put("email", user.getEmail());
		response.getWriter().write(objectMapper.writeValueAsString(userInfo));
	}
}
