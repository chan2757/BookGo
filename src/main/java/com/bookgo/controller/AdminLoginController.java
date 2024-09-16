package com.bookgo.controller;

import com.bookgo.service.PaymentViewService;
import com.bookgo.service.SiteUserService;
import com.bookgo.vo.PaymentViewVO;
import com.bookgo.vo.SiteUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private PaymentViewService paymentViewService;

    @Autowired
    private SiteUserService siteUserService;

    private final AuthenticationManager adminAuthenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminLoginController(
            @Qualifier("adminAuthenticationManager") AuthenticationManager adminAuthenticationManager,
            PasswordEncoder passwordEncoder) {
        this.adminAuthenticationManager = adminAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
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

    @PostMapping("/loginProcess")
    public String adminLoginProcess(@RequestBody Map<String, String> requestBody, Model model) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        try {
            Authentication authentication = adminAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // PaymentViewVO 데이터를 가져와 월별로 그룹화 및 합산
            List<PaymentViewVO> paymentRequests = paymentViewService.getAllPaymentRequests();
            Map<String, Integer> monthlyTotals = calculateMonthlyTotals(paymentRequests);

            // 결과를 모델에 추가하여 메인 페이지로 전달
            model.addAttribute("monthlyTotals", monthlyTotals);

            return "redirect:/admin/main";
        } catch (Exception e) {
            model.addAttribute("error", "로그인에 실패했습니다. 다시 시도해 주세요.");
            return "adminLogin";
        }
    }

    /**
     * PaymentViewVO 데이터를 월별로 그룹화하여 합산하는 메소드
     */

    @GetMapping("/getMonthlyTotals")  // URL 매핑이 정확한지 확인하세요.
    @ResponseBody
    public Map<String, Integer> getMonthlyTotals() {
        List<PaymentViewVO> paymentRequests = paymentViewService.getAllPaymentRequests();

        return calculateMonthlyTotals(paymentRequests);
    }

    @GetMapping("/getMonthlyUserCounts")  // URL 매핑이 정확한지 확인하세요.
    @ResponseBody
    public Map<String, Integer> getMonthlyUserCounts() {
        List<SiteUserVO> users = siteUserService.getAllUsers();
        return calculateMonthlyUserCounts(users);
    }



    private static Map<String, Integer> calculateMonthlyTotals(List<PaymentViewVO> paymentRequests) {
        Map<String, Integer> monthlyTotals = new HashMap<>();
        for (PaymentViewVO request : paymentRequests) {
            String month = request.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyTotals.put(month, monthlyTotals.getOrDefault(month, 0) + request.getTotalAmount());
        }

        return monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private static Map<String, Integer> calculateMonthlyUserCounts(List<SiteUserVO> users) {
        Map<String, Integer> monthlyUserCounts = new HashMap<>();
        for (SiteUserVO user : users) {
            String month = user.getJoinDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyUserCounts.put(month, monthlyUserCounts.getOrDefault(month, 0) + 1);
        }

        return monthlyUserCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }


}

