package com.kosta.readdam.dto.toss;

public class TossPaymentConfirmRequest {
	
	private String paymentKey;
    private String orderId;
    private Integer amount;

    public String getPaymentKey() {
        return paymentKey;
    }
    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

}
