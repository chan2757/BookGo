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
    @GetMapping("/admin")
    public String adminPage() {
        logger.info("관리자 페이지 진입");
        return "admin";
    }

    @GetMapping("/inquiry")
    public String inquiryPage() {
        logger.info("문의 관리 페이지 진입");
        return "inquiry";
    }

    @GetMapping("/member")
    public String memberPage() {
        logger.info("회원 관리 페이지 진입");
        return "member";
    }

    @GetMapping("/inventory")
    public String inventoryPage() {
        logger.info("재고 관리 페이지 진입");
        return "inventory";
    }

		
		
}
