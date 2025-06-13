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

	    String header = request.getHeader(JwtProperties.HEADER_STRING);
	    if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
	        chain.doFilter(request, response); // 토큰 없으면 그냥 넘어가도록
	        return;
	    }

	    String token = header.replace(JwtProperties.TOKEN_PREFIX, "");
	    try {
	        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
	                .build()
	                .verify(token)
	                .getClaim("sub")
	                .asString();

	        if (username != null) {
	            Optional<User> ouser = userRepository.findById(username);
	            if (ouser.isPresent()) {
	                PrincipalDetails principalDetails = new PrincipalDetails(ouser.get());
	                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
	                        principalDetails, null, principalDetails.getAuthorities());
	                SecurityContextHolder.getContext().setAuthentication(auth);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    chain.doFilter(request, response);
	}



	
}
