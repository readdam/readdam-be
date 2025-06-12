package com.kosta.readdam.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.jwt.JwtProperties;
import com.kosta.readdam.jwt.JwtToken;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private static final String URI = "http://localhost:5173/token";
	private JwtToken jwtToken = new JwtToken();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	        Authentication authentication) throws IOException, ServletException {

	    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

	    String username = principalDetails.getUsername();
	    String nickname = principalDetails.getUser().getNickname();
	    Boolean isAdmin = principalDetails.getUser().getIsAdmin();
	    Double lat = principalDetails.getUser().getLat();
	    Double lng = principalDetails.getUser().getLng();

	    // ✅ 잘못된 accessToken → nickname 으로 수정
	    String accessToken = jwtToken.makeAccessToken(username, nickname, isAdmin, lat, lng);
	    String refreshToken = jwtToken.makeRefreshToken(username);

	    ObjectMapper objectMapper = new ObjectMapper();
	    Map<String, String> map = new HashMap<>();
	    map.put("access_token", JwtProperties.TOKEN_PREFIX + accessToken);
	    map.put("refresh_token", JwtProperties.TOKEN_PREFIX + refreshToken);

	    String token = objectMapper.writeValueAsString(map);
	    System.out.println(token);

	    String targetUrl = UriComponentsBuilder.fromUriString(URI)
	            .queryParam("token", token)
	            .build().toString();

	    getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}


}
