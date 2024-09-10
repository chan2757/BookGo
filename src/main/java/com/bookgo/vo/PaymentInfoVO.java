package com.bookgo.vo;

import lombok.Data;

@Data
public class PaymentInfoVO {
    private String buyerEmail;   // 구매자 이메일
    private String buyerName;    // 구매자 이름
    private String buyerTel;     // 구매자 전화번호
    private String buyerAddr;    // 구매자 주소
    private String buyerPostcode; // 구매자 우편번호
    private String productName;  // 결제 상품명
    private int amount; }