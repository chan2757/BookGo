package com.bookgo.mypage.dto;


import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserImpl extends User {
	
	private String memberId;
	private String memberPwd;
	private String name;
	private String phone;
	private String email;
	private String address;
	private char agreement;
	
	private char accInactiveYn;
	private char tempPwdYn;
	private int accumLoginCount;
	private int loginFailedCount;	//로그인연속실패횟수
	private Date latestLoginDate;	//최근로그인일시
	private Date accCreationDate;	//계정가입일자
	private Date accChangedDate;	//계정수정일자
	private Date accClosingDate;	//계정탈퇴일자
	private char accClosingYn;		//계정탈퇴여부
	
	private List<RoleDTO> roleList;
	
	/**
	 * User로부터 상속 받은 생성자 
	 */
	public UserImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	
	/**
	 * 상세 데이터 호출용 메소드
	 */
	public void setDetails(MemberDTO member) {
		this.memberId = member.getMemberId();
		this.memberPwd = member.getMemberPwd();
		this.name = member.getName();
		this.phone = member.getPhone();
		this.email = member.getEmail();
		this.address = member.getAddress();
		this.agreement = member.getAgreement();
		this.accInactiveYn = member.getAccInactiveYn();
		this.tempPwdYn = member.getTempPwdYn();
		this.accumLoginCount = member.getAccumLoginCount();
		this.loginFailedCount = member.getLoginFailedCount();
		this.latestLoginDate = member.getLatestLoginDate();
		this.accCreationDate = member.getAccCreationDate();
		this.accChangedDate = member.getAccChangedDate();
		this.accClosingDate = member.getAccClosingDate();
		this.accClosingYn = member.getAccClosingYn();
	}
}
