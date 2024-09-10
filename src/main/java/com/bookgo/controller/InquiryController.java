package com.bookgo.controller;

import com.bookgo.service.InquiryService;
import com.bookgo.service.SiteUserService; // 유저 정보를 가져오는 서비스 추가
import com.bookgo.vo.InquiryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/voc")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private SiteUserService siteUserService; // 유저 ID를 가져오기 위해 추가

    // 내 문의 내역 페이지로 이동
    @GetMapping("/vocInquiry")
    public String getInquiryPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            int userId = siteUserService.getUserIdByUsername(username); // username으로 userId를 가져오는 서비스 호출
            List<InquiryVO> inquiries = inquiryService.getInquiriesByUserId(userId);

            // 상태를 설정하는 로직 추가
            for (InquiryVO inquiry : inquiries) {
                if (inquiry.getReplyContent() == null || inquiry.getReplyContent().isEmpty()) {
                    inquiry.setStatus("답변준비중");
                } else {
                    inquiry.setStatus("답변완료");
                }
            }

            model.addAttribute("inquiries", inquiries);
        }
        return "voc/vocInquiry";
    }

    // 새로운 문의 생성
    @PostMapping("/inquiry/create")
    public String createInquiry(InquiryVO inquiry, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            int userId = siteUserService.getUserIdByUsername(userDetails.getUsername()); // userId를 가져오는 로직
            inquiry.setUserId(userId);
            inquiry.setUsername(userDetails.getUsername());
            inquiryService.createInquiry(inquiry);
        }
        return "redirect:/voc/vocInquiry";
    }
}
