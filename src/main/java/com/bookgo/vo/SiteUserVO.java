package com.bookgo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class SiteUserVO {

    private Long id;  // 자동 생성되는 필드
    private String nickname;
    private String username;
    private String password;
    private String email;
    private String address1;
    private String address2;
    private LocalDate joinDate = LocalDate.now();  // 기본값 설정
    private LocalDate birthDate;
    @JsonProperty("fullName") // 명시적 매핑 추가
    private String fullName;

    @JsonProperty("phoneNumber") // 명시적 매핑 추가
    private String phoneNumber;

    private int points = 0;
    private String isBanned = "N";
}
