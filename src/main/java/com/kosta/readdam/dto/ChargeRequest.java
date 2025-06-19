package com.kosta.readdam.dto;

public class ChargeRequest {
	private String paymentKey;
    private String orderId;
    private int amount;
    private int point;
    
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
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}

	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	@Override
    public String toString() {
        return "ChargeRequest{" +
               "paymentKey='" + paymentKey + '\'' +
               ", orderId='"    + orderId    + '\'' +
               ", amount="      + amount      +
               ", point="       + point       +
               '}';
    }
    
    

}
