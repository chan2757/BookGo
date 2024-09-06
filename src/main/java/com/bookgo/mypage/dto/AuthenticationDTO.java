package com.bookgo.mypage.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AuthenticationDTO {

	private String memberId;
	private char authPhoneYn;
	private Date authDate;
}
