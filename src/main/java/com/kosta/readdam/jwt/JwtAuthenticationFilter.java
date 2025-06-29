package com.kosta.readdam.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtToken jwtToken = new JwtToken();
	private String fcmToken;
	private final UserRepository userRepository;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
		setFilterProcessesUrl("/loginProc"); // POST 요청 경로 설정
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			// JSON 파싱
			ObjectMapper om = new ObjectMapper();
			Map<String, String> credentials = om.readValue(request.getInputStream(), Map.class);
			String username = credentials.get("username");
			String password = credentials.get("password");
			this.fcmToken = credentials.get("fcmToken");

			// 인증 시도
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
					password);

			return this.getAuthenticationManager().authenticate(authenticationToken);

		} catch (IOException e) {
			throw new RuntimeException("로그인 요청 JSON 파싱 실패", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		User user = principalDetails.getUser();

		if (fcmToken != null && !fcmToken.isBlank()) {
			user.setFcmToken(fcmToken);
			userRepository.save(user);
		}

		String username = user.getUsername();
		String nickname = user.getNickname();
		Boolean isAdmin = user.getIsAdmin();
//		Double lat = user.getLat();
//		Double lng = user.getLng();

		String accessToken = jwtToken.makeAccessToken(username, nickname, isAdmin);
		String refreshToken = jwtToken.makeRefreshToken(username);

		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + accessToken);

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("username", username);
		responseBody.put("nickname", nickname);
		responseBody.put("isAdmin", isAdmin);
//		responseBody.put("lat", lat);
//		responseBody.put("lng", lng);
		responseBody.put("refresh_token", JwtProperties.TOKEN_PREFIX + refreshToken);
		responseBody.put("access_token", JwtProperties.TOKEN_PREFIX + accessToken);

		response.setContentType("application/json; charset=utf-8");
		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(responseBody));
	}
}
