package com.bookgo.service;

import com.bookgo.mapper.SiteUserMapper;
import com.bookgo.vo.SiteUserVO;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Transactional
@Service
public class SiteUserService {

    private static final Logger logger = LoggerFactory.getLogger(SiteUserService.class);

    @Autowired
    private SiteUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService; // EmailService 주입

    // 사용자 등록 메서드
    public void registerUser(SiteUserVO user) {

        System.out.println("Inserting user: " + user); // 추가한 로그
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        System.out.println("Inserting user: " + user); // 추가한 로그
        user.setPoints(0); // 포인트 기본값
        user.setIsBanned("N"); // 밴 여부 기본값

        System.out.println("Inserting user: " + user); // 추가한 로그

        try {
            userMapper.insertUser(user);
            System.out.println("사용자 등록 완료: " + user);
        } catch (Exception e) {
            System.err.println("사용자 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 로그인 검증 메서드
    // 로그인 검증 메서드
    public boolean validateUser(String username, String rawPassword) {
        System.out.println("로그인 검증 시작: 아이디 - " + username);

        String storedPassword = userMapper.getPasswordByUsername(username);

        if (storedPassword == null) {
            System.out.println("아이디가 존재하지 않습니다: " + username);
            return false;
        }

        boolean isPasswordMatch = passwordEncoder.matches(rawPassword, storedPassword);
        System.out.println("비밀번호 일치 여부: " + isPasswordMatch);

        return isPasswordMatch;
    }
    // 이메일로 아이디 찾기 메서드
    public String findUsernameByEmail(String email) {

        System.out.println("Received request to find username with email: " + email);
        try {
            // 이메일로 아이디를 조회
            String username = userMapper.getUsernameByEmail(email);

            // 조회된 결과가 없는 경우 처리 (필요 시)
            if (username == null) {
                System.out.println("등록된 이메일이 아닙니다: " + email);
            }

            return username;

        } catch (Exception e) {
            // 오류 발생 시 메시지 출력 및 로그 기록
            System.err.println("아이디 찾기 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null; // 오류 발생 시 null 반환 (또는 적절한 오류 처리를 추가)
        }
    }


    // 비밀번호 업데이트 메서드
    public void updatePassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(username, encodedPassword);
    }

    // 이메일로 비밀번호 재설정 메일 발송 메서드
    public boolean sendPasswordResetEmail(String username, String email) throws MessagingException, javax.mail.MessagingException {
        SiteUserVO user = getUserByEmail(email);
        if (user == null || !user.getUsername().equals(username)) {
            return false;
        }
        String tempPassword = generateTemporaryPassword();
        updatePassword(username, tempPassword);
        emailService.sendPasswordResetEmail(email, tempPassword); // EmailService를 사용하여 이메일 발송
        return true;
    }

    // 이메일로 사용자 정보 조회 메서드
    public SiteUserVO getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    // 임시 비밀번호 생성 메서드
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        return tempPassword.toString();
    }

    // 현재 로그인된 사용자의 정보를 가져오는 메서드
    public SiteUserVO getLoggedInUser() {
        // Spring Security에서 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // UserDetails를 통해 사용자의 아이디 가져오기
            Object principal = authentication.getPrincipal();
            String username;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            // 가져온 사용자 아이디로 DB에서 사용자 정보 조회
            return userMapper.getUserByUsername(username);
        }

        return null;
    }

    public boolean verifyPassword(String username, String rawPassword) {
        String storedPassword = userMapper.getPasswordByUsername(username);
        return passwordEncoder.matches(rawPassword, storedPassword);
    }

    // 필드 업데이트 메서드
    public void updateUserField(String username, String field, String value) {
        // 로그 추가
        logger.info("Updating user field - Username: {}, Field: {}, Value: {}", username, field, value);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("username", username); // username을 파라미터로 사용
        updateData.put("field", field);
        updateData.put("value", value);

        try {
            userMapper.updateUserField(updateData); // 매퍼 호출
            logger.info("Successfully updated user field for Username: {}", username);
        } catch (Exception e) {
            logger.error("Error updating user field for Username: {}. Field: {}, Value: {}", username, field, value, e);
        }
    }

    // username을 이용하여 사용자 id 조회
    public int getUserIdByUsername(String username) {
        // userMapper를 사용하여 username으로 id를 조회
        return userMapper.getUserIdByUsername(username);
    }


}
