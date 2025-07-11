package com.kosta.readdam.service.write;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosta.readdam.dto.SpellCheckRequest;
import com.kosta.readdam.dto.SpellCheckResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpellCheckServiceImpl implements SpellCheckService {

	// 부산대 맞춤법 검사기를 호출하여 맞춤법 교정 결과를 가져오는 서비스
	@Override
	public SpellCheckResponse checkSpelling(SpellCheckRequest request) {

		// 결과 객체 생성
		SpellCheckResponse result = new SpellCheckResponse();
		List<SpellCheckResponse.Correction> corrections = new ArrayList<>();
		try {
			// 맞춤법 검사기 API URL 생성
			URI uri = UriComponentsBuilder.fromUriString("https://speller.cs.pusan.ac.kr/results")
					.queryParam("text", request.getText()).queryParam("output", "json").build().toUri();

			log.info("부산대 맞춤법 검사 API 호출 URL = {}", uri);

			// ① POST 파라미터 준비
			String body = "text=" + URLEncoder.encode(request.getText(), StandardCharsets.UTF_8)
			            + "&output=json";

			// ② HttpClient 준비 (타임아웃 30초 권장)
			HttpClient client = HttpClient.newBuilder()
			        .connectTimeout(Duration.ofSeconds(30))
			        .build();

			// ③ POST 요청 생성
			HttpRequest httpRequest = HttpRequest.newBuilder()
			        .uri(URI.create("https://speller.cs.pusan.ac.kr/results"))
			        .timeout(Duration.ofSeconds(30))
			        .header("Content-Type", "application/x-www-form-urlencoded")
			        .POST(HttpRequest.BodyPublishers.ofString(body))
			        .build();

			// API 호출
			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			log.info("부산대 API 응답 코드 = {}", response.statusCode());
			// 응답 바디 추출
			String responseBody = response.body();
			log.info("부산대 API 응답 BODY 존재 여부 = {}", responseBody != null);
			log.info("부산대 API 응답 BODY = {}", responseBody);
			
			if (responseBody != null && responseBody.trim().startsWith("{")) {
			    log.info("JSON 형식 응답임");
			} else {
			    log.warn("JSON 아님 → HTML 응답일 가능성 높음");
			}

			// JSON 파싱
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(responseBody);

			JsonNode resultNode = root.get("errInfo");

			// 교정 정보 추출
			if (resultNode != null && resultNode.isArray()) {
				for (JsonNode node : resultNode) {
					SpellCheckResponse.Correction correction = new SpellCheckResponse.Correction();

					// 원문
					correction.setOrgStr(node.path("orgStr").asText());
					// 추천 교정 단어
					correction.setCandWord(node.path("candWord").asText());
					// 오류 유형
					correction.setErrorType(node.path("errType").asText());
					// 오류 시작 인덱스
					correction.setStart(
							node.has("start") && !node.get("start").isNull() ? node.get("start").asInt() : null);
					// 오류 끝 인덱스
					correction.setEnd(node.has("end") && !node.get("end").isNull() ? node.get("end").asInt() : null);
					// 도움말
					correction.setHelp(node.path("help").asText());

					corrections.add(correction);
				}
			}
		} catch (Exception e) {
			log.error("맞춤법 검사 중 예외 발생", e);
			result.setErrorMessage("맞춤법 검사 중 오류가 발생했습니다.");
		}

		result.setCorrections(corrections);
		return result;

	}

}
