package com.bookgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

@Controller
public class AdminLoginController {

    private final AuthenticationManager adminAuthenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminLoginController(
            @Qualifier("adminAuthenticationManager") AuthenticationManager adminAuthenticationManager,
            PasswordEncoder passwordEncoder) {
        this.adminAuthenticationManager = adminAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            // 사용자 권한(ROLE) 출력
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                Logger logger = LoggerFactory.getLogger(getClass());
                logger.info("현재 로그인된 사용자의 권한: {}", authority.getAuthority());
            }
        } else {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.info("현재 로그인된 사용자가 없습니다.");
        }

        return "admin/adminLogin";  // admin/adminLogin.html로 이동
    }
    @PostMapping("/admin/loginProcess")
    public String adminLoginProcess(@RequestBody Map<String, String> requestBody, Model model) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        try {
            Authentication authentication = adminAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/admin/main";
        } catch (Exception e) {
            model.addAttribute("error", "로그인에 실패했습니다. 다시 시도해 주세요.");
            return "adminLogin";
        }
    }
}
