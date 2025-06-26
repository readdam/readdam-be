package com.kosta.readdam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.kosta.readdam.jwt.JwtAuthenticationFilter;
import com.kosta.readdam.jwt.JwtAuthorizationFilter;
import com.kosta.readdam.oauth.OAuth2SuccessHandler;
import com.kosta.readdam.oauth.PrincipalOAuth2UserService;
import com.kosta.readdam.repository.UserRepository;

@Configuration // IoC 빈(bean) 등록
@EnableWebSecurity //필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
	
	@Autowired
	private CorsFilter corsFilter;
	
	@Autowired
	private PrincipalOAuth2UserService principalOAuth2UserService;
	
	@Autowired
	private OAuth2SuccessHandler oAuth2SuccessHandler;
	
	@Autowired
	private UserRepository userRepository;
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	//Authentication : 인증, 유저 확인(로그인)
	//Authorization : 인가, 유저의 권한
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception{
		http
		.addFilter(corsFilter)
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.formLogin().disable()
		.httpBasic().disable()
		.addFilterAt(new JwtAuthenticationFilter(authenticationManager, userRepository), UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(
			new JwtAuthorizationFilter(authenticationManager, userRepository),
			UsernamePasswordAuthenticationFilter.class
		)
		.oauth2Login()
			.authorizationEndpoint().baseUri("/oauth2/authorization")
			.and()
			.redirectionEndpoint().baseUri("/callback/*")
			.and().userInfoEndpoint().userService(principalOAuth2UserService)
			.and().successHandler(oAuth2SuccessHandler)
		.and()
		.authorizeRequests()
			.antMatchers("/my/**").authenticated()
			.antMatchers("/admin/**").hasRole("ADMIN")
			.anyRequest().permitAll();

		return http.build();
	}
	
	@Bean
	public BCryptPasswordEncoder encoderPassword() {
		return new BCryptPasswordEncoder();
	}

}
