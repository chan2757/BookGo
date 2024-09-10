package com.bookgo.controller;

import com.bookgo.service.EmailService;
import com.bookgo.service.SiteUserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SiteUserService userService;

    // 인증 메일 발송 요청을 처리
    @PostMapping("/sendVerificationEmail")
    public String sendVerificationEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            emailService.sendVerificationEmail(email);
            redirectAttributes.addFlashAttribute("message", "인증 메일이 발송되었습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "메일 발송에 실패하였습니다.");
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/emailVerification";
    }

    // 인증번호 검증 요청을 처리
    // 인증번호 검증 요청을 처리
    @PostMapping("/verifyCode")
    @ResponseBody
    public Map<String, Object> verifyCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        // 디버깅을 위한 로그 추가
        System.out.println("Received email: " + email);
        System.out.println("Received code: " + code);

        boolean isVerified = emailService.verifyCode(email, code);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isVerified);

        if (isVerified) {
            System.out.println("Verification successful!");
        } else {
            System.out.println("Verification failed!");
        }

        return response;
    }



    // 임시 비밀번호 발송 요청을 처리
    @PostMapping("/sendTemporaryPassword")
    public String sendTemporaryPassword(@RequestParam("username") String username, @RequestParam("email") String email, RedirectAttributes redirectAttributes) throws MessagingException, javax.mail.MessagingException {
        boolean result = userService.sendPasswordResetEmail(username, email);
        if (result) {
            redirectAttributes.addFlashAttribute("message", "임시 비밀번호가 발송되었습니다. 로그인 후 비밀번호를 변경해 주세요.");
        } else {
            redirectAttributes.addFlashAttribute("error", "사용자 정보가 일치하지 않습니다.");
        }
        return "redirect:/passwordReset";
    }

    // 이메일로 아이디 찾기 요청을 처리
    @PostMapping("/findUsernameByEmail")
    public String findUsernameByEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        String username = userService.findUsernameByEmail(email);
        if (username != null) {
            redirectAttributes.addFlashAttribute("message", "아이디는 " + username + " 입니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 이메일로 등록된 아이디가 없습니다.");
        }
        return "redirect:/findUsername";
    }
}
