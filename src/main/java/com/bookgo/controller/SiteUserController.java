package com.bookgo.controller;

import com.bookgo.service.SiteUserService;
import com.bookgo.vo.SiteUserVO;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/*")
public class SiteUserController {

    @Autowired
    private SiteUserService userService;

    // 회원가입 페이지를 보여주는 메서드 추가
    @GetMapping("/signup")
    public String showSignupForm() {
        return "user/signup";  // signup.html 페이지를 반환
    }

    @Transactional
    @PostMapping("/signup")
    public String signup(SiteUserVO user, Model model) {
        // 요청 데이터 출력
        System.out.println("Received POST request to /user/signup with body:");
        System.out.println("ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Address1: " + user.getAddress1());
        System.out.println("Address2: " + user.getAddress2());
        System.out.println("JoinDate: " + user.getJoinDate());
        System.out.println("BirthDate: " + user.getBirthDate());
        System.out.println("FullName: " + user.getFullName());
        System.out.println("PhoneNumber: " + user.getPhoneNumber());
        System.out.println("Points: " + user.getPoints());
        System.out.println("IsBanned: " + user.getIsBanned());

        try {
            userService.registerUser(user);
            model.addAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/user/login"; // 회원가입 후 로그인 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "user/signup"; // 오류 발생 시 회원가입 페이지로 다시 이동
        }
    }

    // 로그인 페이지를 보여주는 메서드
    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";  // login.html을 보여줌
    }

    // 로그인 요청을 처리하는 메서드
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        System.out.println("로그인 시도: 아이디 - " + username);

        boolean loginSuccessful = userService.validateUser(username, password);

        if (loginSuccessful) {
            // Spring Security의 인증 컨텍스트에 수동으로 인증을 설정
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>())
            );
            System.out.println("로그인 성공: 아이디 - " + username);
            return "redirect:/bookgo/index";
        } else {
            System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
            model.addAttribute("loginErrorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "user/login";
        }
    }
    // 아이디 찾기 페이지 보여주기
    @GetMapping("/findUsername")
    public String showFindUsernameForm() {
        return "user/findUsername";  // findUsername.html 페이지로 이동
    }

    // 비밀번호 찾기 페이지 보여주기
    @GetMapping("/findPassword")
    public String showFindPasswordForm() {
        return "user/findPassword";  // findPassword.html 페이지로 이동
    }

    @PostMapping("/findUsername")
    @ResponseBody
    public Map<String, Object> findUsername(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        System.out.println("Received request to find username with email: " + email);

        try {
            String username = userService.findUsernameByEmail(email);
            if (username != null) {
                response.put("success", true);
                response.put("username", username);
                System.out.println("Found username: " + username);
            } else {
                response.put("success", false);
                response.put("error", "해당 이메일로 등록된 아이디를 찾을 수 없습니다.");
                System.out.println("No username found for email: " + email);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "아이디 찾기 중 오류가 발생했습니다. 다시 시도해주세요.");
            System.err.println("Error occurred while finding username: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }


    @PostMapping("/findPassword")
    @ResponseBody
    public Map<String, Object> findPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String email = request.get("email");
        System.out.println("Received request to find password with username: " + username + " and email: " + email);

        try {
            boolean success = userService.sendPasswordResetEmail(username, email);
            if (success) {
                response.put("success", true);
                System.out.println("Password reset email sent successfully to: " + email);
            } else {
                response.put("success", false);
                response.put("error", "해당 정보로 등록된 계정을 찾을 수 없습니다.");
                System.out.println("No account found for username: " + username + " and email: " + email);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "비밀번호 재설정 메일 발송 중 오류가 발생했습니다.");
            System.err.println("Error occurred while sending password reset email: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    @GetMapping("/mypage")
    public String myPage() {

        return "user/mypage"; // mypage.html 페이지 반환
    }





    // 사용자 정보 제공 API
    @GetMapping("/info")
    @ResponseBody
    public SiteUserVO getUserInfo() {
        SiteUserVO user = userService.getLoggedInUser();
        if (user == null) {
            throw new RuntimeException("사용자 정보를 가져올 수 없습니다.");
        }
        return user;
    }


    @GetMapping("/infoDetail")
    public ModelAndView infoDetail() {
        SiteUserVO user = userService.getLoggedInUser();

        // 사용자가 없을 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            System.out.println("사용자 정보가 없습니다. 로그인 페이지로 이동합니다.");


            return new ModelAndView("redirect:/user/login"); // 로그인 페이지 경로로 리다이렉트
        }

        // 사용자 정보가 있는 경우 정보를 모델에 담아 반환
        ModelAndView modelAndView = new ModelAndView("user/infoDetail");
        modelAndView.addObject("user", user);
        System.out.println("User Data: " + user);
        return modelAndView;
    }


    @PostMapping("/verifyPassword")
    @ResponseBody
    public Map<String, Object> verifyPassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isMatch = userService.verifyPassword(username, password);

        // 응답 형식을 JSON으로 맞추기
        Map<String, Object> response = new HashMap<>();
        response.put("success", isMatch);
        return response;
    }



    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateUserField(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 현재 로그인된 사용자의 유저네임을 가져옵니다.
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String field = request.get("field").toString().toUpperCase();
            String value = request.get("value").toString();

                System.out.println("Current username: " + username);
                System.out.println("Field to update: " + field);
                System.out.println("New value: " + value);

            // 서비스 로직 호출: 유저네임과 필드, 값을 전달하여 업데이트 수행
            userService.updateUserField(username, field, value);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "업데이트 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return response;
    }


}


