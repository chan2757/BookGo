package com.bookgo.cart.dto;

import lombok.Data;

@Data
public class PaymentDTO {

	private String paymentNo;
	private String orderNo;
	private String paymentMethod;
	private int paymentAmount;
	private String paymentDateTime;
}
