package com.bookgo.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class BookDpVO {
    private String cover; // 표지 이미지 URL
    private String title; // 상품명
    private String publisher; // 출판사
    private List<String> authors; // 저자 리스트로 변경
    private String isbn13; // 13자리 ISBN
    private int priceStandard; // 정가
    private double customerReviewRank; // 회원 리뷰 평점 (0~10점)

    // 저자 문자열을 리스트로 변환하는 메서드
    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.authors = Arrays.asList(author.split(",\\s*"));
        }
    }

    @Override
    public String toString() {
        return "BookDpVO{" +
                "cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", authors=" + authors +
                ", isbn13='" + isbn13 + '\'' +
                ", priceStandard=" + priceStandard +
                ", customerReviewRank=" + customerReviewRank +
                '}';
    }
}
