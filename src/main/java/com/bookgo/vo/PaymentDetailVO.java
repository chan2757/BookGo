package com.bookgo.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentDetailVO {
    private Long detailId;        // 결제 상세 ID (자동 생성)
    private String requestId;     // 결제 요청 ID (ORDER_ID와 매핑)
    private String productName;   // 결제된 상품명
    private String isbn13;        // 상품 ISBN13
    private Integer quantity;     // 결제된 상품 수량
    private Integer itemPrice;    // 단위 가격
    private Integer totalPrice;   // 총 가격 (ITEM_PRICE * QUANTITY)
    private Timestamp createdAt;  // 생성 시간


}
