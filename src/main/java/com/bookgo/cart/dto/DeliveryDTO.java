package com.bookgo.cart.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DeliveryDTO {

	private String orderNo; 		// 주문번호
	private int deliveryFee;		// 배송비
	private String deliveryCompany; // 배송사
	private Date dispatchDate;		// 발송일
	private Date deliveryDate;		// 배송일
}
