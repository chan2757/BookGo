package com.bookgo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDpVO {
    private String cover; // 표지 이미지 URL
    private String title; // 상품명
    private String publisher; // 출판사
    private String author; // 저자
    private String isbn13; // 13자리 ISBN
    private int priceStandard; // 정가
    private double customerReviewRank; // 회원 리뷰 평점 (0~10점)

    @Override
    public String toString() {
        return "BookDpVO{" +
                "cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", author='" + author + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", priceStandard=" + priceStandard +
                ", customerReviewRank=" + customerReviewRank +
                '}';
    }
}
