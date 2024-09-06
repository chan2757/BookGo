package com.bookgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookgo/*")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(BookGoController.class);
	
	@GetMapping("/join")
	public void loginPage() {
		
		logger.info("회원가입 페이지 진입");
		
	}

	@GetMapping("/login")
	public void joinPage() {
		
		logger.info("로그인 페이지 진입");
		
	}
}
