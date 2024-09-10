package com.bookgo.vo;

import lombok.Data;

@Data
public class CartItemVO {
    private String isbn13;   // 책 ISBN13
    private String title;    // 책 제목
    private int quantity;    // 수량
    private int price;       // 가격
    private String cover;    // 책 커버 이미지 URL
}
