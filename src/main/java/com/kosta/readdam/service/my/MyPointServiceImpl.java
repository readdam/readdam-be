package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.PointDto;
import com.kosta.readdam.entity.Order;
import com.kosta.readdam.entity.Point;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.entity.enums.PaymentStatus;
import com.kosta.readdam.repository.OrderRepository;
import com.kosta.readdam.repository.PointRepository;
import com.kosta.readdam.repository.UserRepository;
import com.kosta.readdam.util.TossApiClient;

@Service
public class MyPointServiceImpl implements MyPointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TossApiClient tossApiClient;

    @Override
    public List<PointDto> getMyPointList(String username) throws Exception {
        List<Point> pointList = pointRepository.findByUser_UsernameOrderByDateDesc(username);
        return pointList.stream()
                .map(p -> PointDto.builder()
                        .pointId(p.getPointId())
                        .username(p.getUser().getUsername())
                        .point(p.getPoint())
                        .date(p.getDate())
                        .reason(p.getReason())
                        .orderId(p.getOrder() != null ? p.getOrder().getOrderId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void confirmAndChargePoint(String paymentKey, String orderId, int amount, String username) throws Exception {
        // 1. Toss 결제 검증
        Map<String, Object> res = tossApiClient.verify(paymentKey, orderId, amount);

        // 2. 결제 상태 확인
        String status = (String) res.get("status");
        if (!"DONE".equalsIgnoreCase(status)) {
            throw new IllegalStateException("결제가 완료되지 않았습니다. status=" + status);
        }

        // 3. 유저 조회
        User user = userRepository.findById(username).orElseThrow();

        // 4. 주문 저장
        String method = (String) res.get("method");
        Order order = Order.builder()
                .user(user)
                .paymentStatus(PaymentStatus.PAID)
                .paymentKey(paymentKey)
                .approvedAt(LocalDateTime.now())
                .paymentMethod(method)
                .price(amount)
                .build();
        orderRepository.save(order);

        // 5. 포인트 계산 (정책 기반)
        int pointAmount = getPointAmountByPrice(amount);

        // 6. 포인트 저장
        Point point = Point.builder()
                .user(user)
                .point(pointAmount)
                .reason("포인트 충전")
                .order(order)
                .build();
        pointRepository.save(point);

        // 7. 유저 누적 포인트 반영
        user.setTotalPoint((user.getTotalPoint() != null ? user.getTotalPoint() : 0) + pointAmount);
    }

    private int getPointAmountByPrice(int amount) {
        switch (amount) {
            case 10000: return 500;
            case 20000: return 1100;
            case 30000: return 1800;
            case 50000: return 3250;
            default:
                throw new IllegalArgumentException("지원하지 않는 충전 금액입니다: " + amount);
        }
    }
    
    @Override
    public int getMyTotalPoint(String username) throws Exception{
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + username));
        return user.getTotalPoint() != null ? user.getTotalPoint() : 0;
    }

}
