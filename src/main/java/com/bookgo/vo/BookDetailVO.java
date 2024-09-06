package com.bookgo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDetailVO {
    private String title;
    private String author;
    private String publisher;        // 출판사
    private String pubDate;          // 출판일
    private int priceStandard;       // 정가
    private double customerReviewRank; // 평점
    private String introduction;
    private String category;
    private String authorInfo;
    private String mainImg;          // 메인 이미지
    private String contents;
    private String recommendations;
    private String bookInside;
    private String publisherReview;
    private String isbn13;           // ISBN13 필드
    private String cover;// 표지 이미지 URL (추가)

    @Override
    public String toString() {
        return "BookDetailVO{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", priceStandard=" + priceStandard +
                ", customerReviewRank=" + customerReviewRank +
                ", introduction='" + introduction + '\'' +
                ", category='" + category + '\'' +
                ", authorInfo='" + authorInfo + '\'' +
                ", mainImg='" + mainImg + '\'' +
                ", contents='" + contents + '\'' +
                ", recommendations='" + recommendations + '\'' +
                ", bookInside='" + bookInside + '\'' +
                ", publisherReview='" + publisherReview + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
