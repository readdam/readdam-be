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
import com.kosta.readdam.service.alert.NotificationService;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtToken jwtToken = new JwtToken();
    private String fcmToken;

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        super(authenticationManager);
        this.userRepository      = userRepository;
        this.notificationService = notificationService;
        setFilterProcessesUrl("/loginProc");   // POST 요청 경로
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, String> creds = om.readValue(request.getInputStream(), Map.class);

            String username = creds.get("username");
            String password = creds.get("password");
            this.fcmToken   = creds.get("fcmToken");

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 JSON 파싱 실패", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
                                            throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        User user = principalDetails.getUser();

        // FCM 토큰 저장
        if (fcmToken != null && !fcmToken.isBlank()) {
            user.setFcmToken(fcmToken);
            userRepository.save(user);
        }

        // 로그인 직후 요약 푸시 전송(제목·본문 null ⇒ 규칙 적용)
        notificationService.sendPush(user.getUsername(), null, null, null);

        String username = user.getUsername();
        String nickname = user.getNickname();
        Boolean isAdmin = user.getIsAdmin();
        // Double lat = user.getLat();
        // Double lng = user.getLng();

        String accessToken  = jwtToken.makeAccessToken(username, nickname, isAdmin);
        String refreshToken = jwtToken.makeRefreshToken(username);

        response.addHeader(JwtProperties.HEADER_STRING,
                           JwtProperties.TOKEN_PREFIX + accessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("nickname", nickname);
        body.put("isAdmin",  isAdmin);
        // body.put("lat", lat);
        // body.put("lng", lng);
        body.put("refresh_token", JwtProperties.TOKEN_PREFIX + refreshToken);
        body.put("access_token",  JwtProperties.TOKEN_PREFIX + accessToken);

        response.setContentType("application/json; charset=utf-8");
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
