package com.kosta.readdam.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.UserRepository;

//인가 : 로그인 처리가 되어야만 하는 처리가 들어왔을때 실행
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	
	private UserRepository userRepository;
	
	private JwtToken jwtToken = new JwtToken();
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager,UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
 		String uri = request.getRequestURI();
 		System.out.println(uri);
 		
 		//로그인(인증)이 필요없는 요청은 그대로 진행
 		if(uri.contains("/detail")) {
 			if(request.getHeader(JwtProperties.HEADER_STRING)==null) {
 	 			chain.doFilter(request, response);
 	 			return;
 			}
 		} else if(!(uri.contains("/user") || uri.contains("/admin") || uri.contains("/manager"))) {
 			chain.doFilter(request, response);
 			return;
 		} 
 		
		String authentication = request.getHeader(JwtProperties.HEADER_STRING);
		if(authentication==null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"로그인 필요");
			return;
		}
		System.out.println(authentication);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,String> token = objectMapper.readValue(authentication,Map.class);
		System.out.println(token);
		
		//access_token : header로부터 accessToken가져와 bear check
		String accessToken = token.get("access_token");
		if(!accessToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
			return;
		}
		
		accessToken = accessToken.replace(JwtProperties.TOKEN_PREFIX, "");
		try {
			//1. access token check
			//1-1. 보안키, 만료시간 체크
			String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
						.build()
						.verify(accessToken)
						.getClaim("sub")
						.asString();
			System.out.println(username);
			if(username==null || username.equals("")) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
				return;
			}
			
			//1-2. username check		
			Optional<User> ouser = userRepository.findById(username);
			if(ouser.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
				return;
			}
			
			//1-3. User를 Authentcation으로 생성해서 Security session(Context Holder)에 넣어준다.(그러면, Controller에서 사용할 수 있다.)
			PrincipalDetails principalDetails = new PrincipalDetails(ouser.get());
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principalDetails, null,
					principalDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			chain.doFilter(request, response);
			return;
			
			
		} catch(Exception e) {
			e.printStackTrace();
			try {
				//2. Refresh Token check : Access Token invalidate 일 경우
				String refreshToken = token.get("refresh_token");
				if(!refreshToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
					return;
				}
				
				refreshToken = refreshToken.replace(JwtProperties.TOKEN_PREFIX, "");
				//2-1. 보안키, 만료시간 check
				String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
					.build()
					.verify(refreshToken)
					.getClaim("sub")
					.asString();
				
				//2-2. username check
				Optional<User> ouser = userRepository.findById(username);
				if(ouser.isEmpty()) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
					return;
				}
				
				//3-3. refresh token이 invalidate일 경우 accessToken, refreshToken 새로 만들어서 보낸다.
				User user = ouser.get();  // 이미 Optional에서 꺼낸 사용자 객체
				String nickname = user.getNickname();
				Boolean isAdmin = user.getIsAdmin();
				Double lat = user.getLat();
				Double lng = user.getLng();

				String reAccessToken = jwtToken.makeAccessToken(username, nickname, isAdmin, lat, lng);
				String reRefreshToken = jwtToken.makeRefreshToken(username);
				
				Map<String,String> map = new HashMap<>();
				map.put("access_token", JwtProperties.TOKEN_PREFIX+reAccessToken);
				map.put("refresh_token", JwtProperties.TOKEN_PREFIX+reRefreshToken);

				PrincipalDetails principalDetails = new PrincipalDetails(ouser.get());
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principalDetails, null,
						principalDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				
				String reToken = objectMapper.writeValueAsString(map);				
				response.addHeader(JwtProperties.HEADER_STRING, reToken);				
			} catch(Exception e2) {
				e2.printStackTrace();
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
			}
		}
		
		super.doFilterInternal(request, response, chain);
	}
}
