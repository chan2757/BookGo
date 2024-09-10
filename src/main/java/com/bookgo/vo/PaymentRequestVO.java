package com.bookgo.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentRequestVO {
    private Long requestId;        // 결제 요청 ID (시퀀스를 통해 자동 생성)
    private String orderId;        // 주문 ID (order_username_UUID 형식)
    private String username;       // 결제를 요청한 사용자 username
    private String fullName;       // 사용자의 이름
    private String email;          // 사용자의 이메일
    private String phone;          // 사용자의 전화번호
    private String address;        // 사용자의 주소
    private String tid; // tid 필드 추가
    private Integer totalAmount;   // 결제 총액
    private Timestamp createdAt;
}
