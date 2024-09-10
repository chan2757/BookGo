package com.bookgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // 관리자 메인 페이지
    @GetMapping("/main")
    public String adminMain() {
        // adminMain.html 페이지로 이동

        return "admin/adminMain";
    }

    // 관리자 생성 페이지
    @GetMapping("/adminCreate")
    public String adminCreate() {
        // adminCreate.html 페이지로 이동
        return "admin/adminCreate";
    }

    // 문의 관리 페이지
    @GetMapping("/inquiry")
    public String inquiryManagement() {
        // inquiryManagement.html 페이지로 이동
        return "admin/inquiry";
    }

    // 회원 관리 페이지
    @GetMapping("/member")
    public String memberManagement() {
        // memberManagement.html 페이지로 이동
        return "admin/member";
    }

    // 재고 관리 페이지
    @GetMapping("/inventory")
    public String inventoryManagement() {
        // inventoryManagement.html 페이지로 이동
        return "admin/inventory";
    }
}
