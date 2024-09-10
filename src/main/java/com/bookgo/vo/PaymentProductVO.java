package com.bookgo.vo;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PaymentProductVO {
    private String productName;   // 상품 이름
    private String isbn13;        // ISBN13
    private Integer quantity;     // 수량
    private int itemPrice;     // 단가
    private int totalPrice;    // 총 가격
    private Timestamp createdAt;  // 생성일 (필요 시 사용)
}