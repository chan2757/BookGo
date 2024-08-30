package com.bookgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookgo/*")
public class BookGoController {
	private static final Logger logger = LoggerFactory.getLogger(BookGoController.class);
	
	@GetMapping("/index")
	public void indexPage() {
		
		logger.info("메인 페이지 진입");
	}
		
}
