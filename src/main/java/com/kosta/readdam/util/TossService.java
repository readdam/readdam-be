package com.kosta.readdam.util;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TossService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RestTemplate rt = new RestTemplate();

    @Value("${toss.clientKey}")
    private String clientKey;
    @Value("${toss.secretKey}")
    private String secretKey;

    /**
     * Toss 결제 검증
     * @param paymentKey Toss가 발급한 결제 키
     * @param orderId    내가 생성한 주문 UUID
     * @param price      요청한 결제 금액
     */
    public void verify(String paymentKey, String orderId, int price) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;

        // Basic Auth 헤더 구성
        String auth = clientKey + ":" + secretKey;
        String base64Auth = Base64.getEncoder().encodeToString(auth.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Toss API 호출
        ResponseEntity<Map> resp = rt.exchange(url, HttpMethod.GET, request, Map.class);
        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("토스 결제 조회 실패: HTTP " + resp.getStatusCode());
        }

        // 응답 바디에서 필수 정보 파싱
        Map<String,Object> body          = resp.getBody();
        String status                    = (String) body.get("status");
        String returnedOrderId           = (String) body.get("orderId");
        String currency                  = (String) body.get("currency");
        Number paidAmtNum                = (Number) body.get("totalAmount");

        // 널 체크
        if (returnedOrderId == null) {
            throw new RuntimeException("결제 정보에 orderId 필드가 없습니다.");
        }
        if (currency == null) {
            throw new RuntimeException("결제 정보에 currency 필드가 없습니다.");
        }
        if (paidAmtNum == null) {
            throw new RuntimeException("결제 정보에 totalAmount 필드가 없습니다.");
        }

        int paidAmount = paidAmtNum.intValue();

        log.info("Toss verify → expectedOrderId={}, returnedOrderId={}, status={}, paidAmount={}, currency={}",
                 orderId, returnedOrderId, status, paidAmount, currency);

        // 1) 상태 검증
        if (!List.of("DONE","PAID","APPROVED","COMPLETED","IN_PROGRESS").contains(status)) {
            throw new RuntimeException("결제 검증 실패: 허용되지 않은 상태(" + status + ")");
        }
        // 2) orderId 검증
        if (!orderId.equals(returnedOrderId)) {
            throw new RuntimeException(
                String.format("Order ID 불일치: 요청 %s, 실제 %s", orderId, returnedOrderId));
        }
        // 3) 통화 검증 (KRW 고정 사용 시)
        if (!"KRW".equals(currency)) {
            throw new RuntimeException(
                String.format("Currency 불일치: 예상 KRW, 실제 %s", currency));
        }
        // 4) 금액 검증
        if (paidAmount != price) {
            throw new RuntimeException(
                String.format("결제 금액 불일치: 요청 %d원, 실제 %d원", price, paidAmount));
        }
    }
}
