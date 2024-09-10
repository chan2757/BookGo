package com.bookgo.vo;

import lombok.Data;

@Data
public class UserInputVO {

    private String username;  // 사용자 계정 이름
    private String password;  // 사용자 비밀번호 (원본)
    private String email;     // 사용자 이메일 주소
    private String address1;  // 사용자 주소 정보
    private String address2;  // 사용자 주소 정보
    private String nickname;  // 사용자의 별명
    private String fullName;  // 사용자 이름
    private String birthDate; // 생년월일
    private String phoneNumber;// 전화번호

}
