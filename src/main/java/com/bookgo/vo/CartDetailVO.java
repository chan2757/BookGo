package com.bookgo.vo;

import lombok.Data;

@Data
public class CartDetailVO {
    private String isbn13;      // 책 ISBN13
    private String title;       // 책 제목
    private String authors;     // 저자
    private String publisher;   // 출판사
    private int price;          // 가격
    private String cover;       // 책 커버 이미지 URL
    private int quantity;       // 장바구니에 담긴 수량
    private int cartId;
}