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
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private SiteUserMapper siteUserMapper;

    // 사용자 등록 메서드
    public void registerUser(SiteUserVO user) {
        System.out.println("Inserting user: " + user); // 추가한 로그
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPoints(0); // 포인트 기본값
        user.setIsBanned("N"); // 밴 여부 기본값

        try {
            userMapper.insertUser(user);
            System.out.println("사용자 등록 완료: " + user);
        } catch (Exception e) {
            System.err.println("사용자 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
            String username = userMapper.getUsernameByEmail(email);
            if (username == null) {
                System.out.println("등록된 이메일이 아닙니다: " + email);
            }
            return username;
        } catch (Exception e) {
            System.err.println("아이디 찾기 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 비밀번호 업데이트 메서드
    public void updatePassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(username, encodedPassword);
    }

    // 이메일로 비밀번호 재설정 메일 발송 메서드
    public boolean sendPasswordResetEmail(String username, String email) throws MessagingException {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
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
        logger.info("Updating user field - Username: {}, Field: {}, Value: {}", username, field, value);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("username", username);
        updateData.put("field", field);
        updateData.put("value", value);

        try {
            userMapper.updateUserField(updateData);
            logger.info("Successfully updated user field for Username: {}", username);
        } catch (Exception e) {
            logger.error("Error updating user field for Username: {}. Field: {}, Value: {}", username, field, value, e);
        }
    }

    // username을 이용하여 사용자 id 조회
    public int getUserIdByUsername(String username) {
        return userMapper.getUserIdByUsername(username);
    }

    @PostMapping("/loginProcess")
    public String loginProcess() {
        // 이 메서드는 필요하지 않으며, Spring Security에서 처리하게 설정
        return "redirect:/index";
    }

    // 사용자 정보를 username으로 조회하는 메서드 추가
    public SiteUserVO getUserByUsername(String username) {
        // MyBatis Mapper를 호출하여 사용자 정보를 가져옵니다.
        return userMapper.getUserByUsername(username);
    }

    public List<SiteUserVO> getAllUsers() {
        return siteUserMapper.getAllUsers();
    }

    // 이메일 중복 확인 메서드
    public boolean checkEmailExists(String email) {
        return userMapper.countByEmail(email) > 0; // userMapper의 countByEmail 메서드를 사용하여 이메일 중복 여부 확인
    }

    // 아이디 중복 확인 메서드
    public boolean checkUsernameExists(String username) {
        return userMapper.countByUsername(username) > 0; // userMapper의 countByUsername 메서드를 사용하여 아이디 중복 여부 확인
    }
}
