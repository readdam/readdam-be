package com.kosta.readdam.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//카카오 지도 API를 호출해서 좌표 → 행정 구역명(주소)으로 변환
@Service
public class GeoServiceImpl implements GeoService {

	@Value("${kakao.api.key}")
	private String kakaoRestApiKey;
	
	@Override
	public String reverseGeocode(Double lat, Double lng) throws Exception {
	    String kakaoUrl =
	            "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json"
	            + "?x=" + lng
	            + "&y=" + lat;

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<Map> response = restTemplate.exchange(
	            kakaoUrl,
	            HttpMethod.GET,
	            entity,
	            Map.class
	        );

	        Map body = response.getBody();

	        if (body == null) {
	            throw new Exception("카카오 API 응답이 없습니다.");
	        }

	        List<Map<String, Object>> documents =
	            (List<Map<String, Object>>) body.get("documents");

	        if (documents != null && !documents.isEmpty()) {
	            Map<String, Object> doc = documents.get(0);
	            String address = (String) doc.get("address_name");
	            return address;
	        } else {
	            throw new Exception("주소 정보를 찾을 수 없습니다.");
	        }
	    }
}