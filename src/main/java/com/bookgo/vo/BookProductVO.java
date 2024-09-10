package com.bookgo.vo;

import lombok.Data;

@Data
public class BookProductVO {
    private String isbn13;          // ISBN13
    private String title;           // 책 제목
    private String authors;         // 저자
    private String publisher;       // 출판사
    private int priceStandard;      // 가격
    private String cover;           // 커버 이미지 경로
    private int stock;              // 재고 수량
}
