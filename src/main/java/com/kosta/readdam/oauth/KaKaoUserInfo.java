package com.kosta.readdam.oauth;

import java.util.Map;

public class KaKaoUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes;
	private Map<String, Object> properties;
	
	public KaKaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		properties = (Map<String,Object>)attributes.get("properties");
	}

	@Override
	public String getProviderId() {
		return String.valueOf(attributes.get("id"));
	}

	@Override
	public String getProvider() {
		// TODO Auto-generated method stub
		return "Kakao";
	}

	@Override
	public String getEmail() {
		return "";
	}

	@Override
	public String getName() {
		return (String)(properties.get("nickname"));
	}

	@Override
	public String getProfileImage() {
		return (String)attributes.get("profile_image");
	}

}
