package com.bookgo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 인증번호를 임시로 저장할 맵
    private Map<String, String> verificationCodes = new HashMap<>();

    // 인증번호 생성 메서드
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 인증 메일 전송 메서드
    public void sendVerificationEmail(String email) throws MessagingException {
        String verificationCode = generateVerificationCode();
        verificationCodes.put(email, verificationCode);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("yoyoung555@naver.com");
        helper.setTo(email);
        helper.setSubject("이메일 인증 요청");
        helper.setText("<h1>이메일 인증</h1><p>아래 인증번호를 입력하여 이메일 인증을 완료하세요:</p>" +
                "<p><strong>인증번호: " + verificationCode + "</strong></p>", true);

        mailSender.send(message);
    }

    // 인증번호 검증 메서드
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    // 임시 비밀번호 생성 메서드
    public String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        return tempPassword.toString();
    }

    // 비밀번호 재설정 안내 메일 전송 메서드
    public void sendPasswordResetEmail(String email, String tempPassword) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("yoyoung555@naver.com");
        helper.setTo(email);
        helper.setSubject("비밀번호 재설정 안내");
        helper.setText("<h1>비밀번호 재설정</h1>" +
                "<p>임시 비밀번호: <strong>" + tempPassword + "</strong></p>" +
                "<p>로그인 후 반드시 비밀번호를 변경해 주세요.</p>", true);

        mailSender.send(message);
    }
}
