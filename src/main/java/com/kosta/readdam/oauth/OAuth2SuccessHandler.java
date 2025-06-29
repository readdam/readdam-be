package com.kosta.readdam.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.jwt.JwtProperties;
import com.kosta.readdam.jwt.JwtToken;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${oauth2.redirect.uri:http://localhost:5173/}")
	private String redirectUri;

	private final JwtToken jwtToken = new JwtToken();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

		String username = principalDetails.getUsername();
		String nickname = principalDetails.getUser().getNickname();
		Boolean isAdmin = principalDetails.getUser().getIsAdmin();
		Double lat = principalDetails.getUser().getLat();
		Double lng = principalDetails.getUser().getLng();

		String accessToken = jwtToken.makeAccessToken(username, nickname, isAdmin);
		String refreshToken = jwtToken.makeRefreshToken(username);

		// access_token도 쿠키로 내려줌 (HttpOnly 아님!)
		Cookie accessCookie = new Cookie("access_token", accessToken);
		accessCookie.setHttpOnly(false); // JS에서 읽게 해야 함
		accessCookie.setSecure(true);    // https면 true, http 개발 환경이면 false로 변경 가능
		accessCookie.setPath("/");
		accessCookie.setMaxAge(60 * 60); // 1시간
		response.addCookie(accessCookie);

		// refresh_token은 기존처럼 유지
		Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
		refreshCookie.setHttpOnly(true); // JS에서 못 읽게 → 보안 목적
		refreshCookie.setSecure(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
		response.addCookie(refreshCookie);

		// redirect (JSON 응답은 제거)
		response.sendRedirect("http://localhost:5173/oauth-redirect?access_token=" + accessToken);


	}

}
