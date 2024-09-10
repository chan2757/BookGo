package com.bookgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BookGoController {
	private static final Logger logger = LoggerFactory.getLogger(BookGoController.class);

	// 메인 페이지 매핑 메서드
	@GetMapping("/index")
	public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails != null) {
			// 로그인된 사용자의 이름(또는 사용자 정보를 가져올 수 있습니다)
			model.addAttribute("username", userDetails.getUsername());
		}
		return "bookgo/index"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/shop")
	public String booklist() {
		logger.info("리스트 페이지 진입");
		return "bookgo/booksearch"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/map")
	public String map() {
		logger.info("도서관지도 페이지 진입");
		return "bookgo/map"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/login")
	public String login() {
		logger.info("로그인 페이지 진입");
		return "user/login"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/myinfo")
	public String myinfo() {
		logger.info("나의 정보 페이지 진입");
		return "user/infoDetail"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/signup")
	public String signup() {
		logger.info("회원가입 페이지 진입");
		return "user/signup"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/mypagemenu")
	public String mypagemenu() {
		logger.info("마이페이지메뉴 페이지 진입");
		return "user/mypage"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/contact")
	public String contact() {
		logger.info("고객센터 페이지 진입");
		return "voc/vocMain"; // templates/bookgo/index.html로 매핑
	}

	// FAQ 게시판 페이지로 이동
	@GetMapping("/voc/vocFAQ")
	public String vocFAQ(Model model) {
		// 필요한 경우, 페이지 로딩 시 필요한 데이터를 Model에 추가할 수 있습니다.
		return "voc/vocFAQ"; // templates 폴더에 있는 vocFAQ.html로 연결
	}




		
}
