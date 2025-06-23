package com.kosta.readdam.service.my;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.PointDto;
import com.kosta.readdam.dto.RefundRequest;
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
    public List<PointDto> getMyPointList(String username) {
        return pointRepository.findByUser_UsernameOrderByDateDesc(username)
            .stream()
            .map(Point::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public int getMyTotalPoint(String username) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + username));
        return user.getTotalPoint() == null ? 0 : user.getTotalPoint();
    }

    @Override
    @Transactional
    public String createOrder(User user, int point, int price) {
        String orderUuid = UUID.randomUUID().toString();
        Order order = Order.builder()
            .orderUuid(orderUuid)
            .orderName(point + "P 충전")
            .user(user)
            .price(price)
            .paymentStatus(PaymentStatus.PENDING)
            .paymentMethod("")
            .requestedAt(LocalDateTime.now())
            .build();
        orderRepository.save(order);
        return orderUuid;
    }

    /**
     * 결제 검증 후 Order 상태 업데이트, User 포인트 적립, Point 내역 저장
     */
    @Transactional
    @Override
    public void verifyAndSave(String paymentKey, String orderUuid, int point, User user) throws Exception {
        // 주문 조회
        Order order = orderRepository.findByOrderUuid(orderUuid)
            .orElseThrow(() -> {
                String msg = "Order not found by UUID: " + orderUuid;
               	System.out.println(msg);
                return new RuntimeException(msg);
            });

        // 주문 업데이트
        order.setPaymentKey(paymentKey);
        order.setPaymentStatus(PaymentStatus.APPROVED);
        order.setApprovedAt(LocalDateTime.now());
        orderRepository.save(order);

        // 유저 포인트 증액
        Integer current = user.getTotalPoint();                   // 이 값이 null일 수 있음
        int newTotal = (current == null ? 0 : current) + point;  // null 체크!
        user.setTotalPoint(newTotal);
        userRepository.save(user);

        // 내역 저장
        Point p = Point.builder()
            .user(user)
            .order(order)
            .point(point)
            .reason(point + "P 충전")
            .build();
        pointRepository.save(p);
    }
    
    @Override
    @Transactional
    public void refund(RefundRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getPaymentStatus() != PaymentStatus.APPROVED) {
            throw new IllegalStateException("환불 처리 가능 상태가 아닙니다.");
        }

        // ① 충전 레코드 조회
        Point chargeRecord = pointRepository
            .findTopByOrderAndPointGreaterThanOrderByDateDesc(order, 0)
            .orElseThrow(() -> new IllegalStateException("충전 내역을 찾을 수 없습니다."));

        int chargedPoints = chargeRecord.getPoint();

        // ② 주문 상태 업데이트
        order.setPaymentStatus(PaymentStatus.CANCELLED);
        order.setCancelReason(req.getReason());
        order.setCancelAt(LocalDateTime.now());
        orderRepository.save(order);

        // ③ 포인트 환불 레코드 생성 (충전된 포인트 양만큼 마이너스)
        Point refundPoint = Point.builder()
            .user(order.getUser())
            .point(-chargedPoints)        // ← price가 아니라 chargedPoints 사용
            .reason("환불")
            .order(order)
            .build();
        pointRepository.save(refundPoint);
    }

}
