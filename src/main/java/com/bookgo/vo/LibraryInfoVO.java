package com.bookgo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryInfoVO {
    private Long LIB_ID;  // LIB_ID - 도서관 ID
    private String LIB_NAME;  // 도서관 이름
    private String LIB_CITY;  // 도서관이 위치한 도시
    private String LIB_DISTRICT;  // 도서관이 위치한 구역
    private String LIB_TYPE;  // 도서관 유형
    private Double LIB_LATITUDE;  // 도서관의 위도
    private Double LIB_LONGITUDE;  // 도서관의 경도
    private String LIB_PHONENUMBER;  // 도서관 전화번호
    private String LIB_WEBSITE;  // 도서관 웹사이트
    private Boolean LIB_LOANAVAILABILITY;  // 대출 가능 여부
    private String LIB_OPERATIONDETAILS;  // 운영 세부사항

    @Override
    public String toString() {
        return "LibraryInfoVO{" +
                "LIB_ID=" + LIB_ID +
                ", LIB_NAME='" + LIB_NAME + '\'' +
                ", LIB_CITY='" + LIB_CITY + '\'' +
                ", LIB_DISTRICT='" + LIB_DISTRICT + '\'' +
                ", LIB_TYPE='" + LIB_TYPE + '\'' +
                ", LIB_LATITUDE=" + LIB_LATITUDE +
                ", LIB_LONGITUDE=" + LIB_LONGITUDE +
                ", LIB_PHONENUMBER='" + LIB_PHONENUMBER + '\'' +
                ", LIB_WEBSITE='" + LIB_WEBSITE + '\'' +
                ", LIB_LOANAVAILABILITY=" + LIB_LOANAVAILABILITY +
                ", LIB_OPERATIONDETAILS='" + LIB_OPERATIONDETAILS + '\'' +
                '}';
    }
}
