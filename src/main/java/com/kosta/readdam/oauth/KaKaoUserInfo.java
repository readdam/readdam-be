package com.kosta.readdam.oauth;

import java.util.Map;

public class KaKaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;
    private Map<String, Object> properties;
    private Map<String, Object> kakaoAccount;
    private Map<String, Object> profile;

    public KaKaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.properties = (Map<String, Object>) attributes.get("properties");
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount != null && kakaoAccount.get("profile") != null) {
            this.profile = (Map<String, Object>) kakaoAccount.get("profile");
        }
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "Kakao";
    }

    @Override
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.get("email") != null) {
            return (String) kakaoAccount.get("email");
        }
        return "";
    }

    @Override
    public String getName() {
        if (profile != null && profile.get("nickname") != null) {
            return (String) profile.get("nickname");
        }
        if (properties != null && properties.get("nickname") != null) {
            return (String) properties.get("nickname");
        }
        return "";
    }

    @Override
    public String getProfileImage() {
        if (profile != null && profile.get("profile_image_url") != null) {
            return (String) profile.get("profile_image_url");
        }
        if (properties != null && properties.get("profile_image") != null) {
            return (String) properties.get("profile_image");
        }
        return "";
    }
}
