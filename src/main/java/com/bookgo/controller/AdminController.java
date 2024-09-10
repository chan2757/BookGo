package com.bookgo.controller;

import com.bookgo.service.BookProductService;
import com.bookgo.service.ExcelExportService;
import com.bookgo.service.InquiryService;
import com.bookgo.service.PaymentViewService; // 결제 데이터를 가져오는 서비스 추가
import com.bookgo.service.SiteUserService;
import com.bookgo.vo.BookProductVO;
import com.bookgo.vo.InquiryVO;
import com.bookgo.vo.PaymentViewVO; // 결제 요청 VO 추가
import com.bookgo.vo.SiteUserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private BookProductService bookProductService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private SiteUserService siteUserService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private PaymentViewService paymentViewService; // 결제 데이터 서비스 추가

    // 관리자 메인 페이지
    @GetMapping("/main")
    public String adminMain() {
        return "admin/adminMain";
    }

    // 결제 관리 페이지
    @GetMapping("/pay")
    public String paymentManagement(Model model) {
        logger.info("Accessing payment management page.");

        // 결제 요청 데이터 조회
        List<PaymentViewVO> paymentRequests = paymentViewService.getAllPaymentRequests();
        logger.debug("Fetched payment requests: {}", paymentRequests);

        if (paymentRequests == null || paymentRequests.isEmpty()) {
            logger.warn("No payment requests found.");
        } else {
            logger.info("Number of payment requests fetched: {}", paymentRequests.size());
        }

        // 모델에 결제 요청 데이터를 추가하여 뷰에 전달합니다.
        model.addAttribute("paymentRequests", paymentRequests);
        return "admin/adminPay"; // adminPay.html로 이동
    }

    // 관리자 생성 페이지
    @GetMapping("/adminCreate")
    public String adminCreate() {
        return "admin/adminCreate";
    }

    // 관리자 문의 관리 페이지
    @GetMapping("/inquiry")
    public String inquiryManagement(Model model) {
        logger.info("Accessing inquiry management page.");
        List<InquiryVO> inquiries = inquiryService.getAllInquiries();
        logger.debug("Fetched inquiries: {}", inquiries);

        if (inquiries == null || inquiries.isEmpty()) {
            logger.warn("No inquiries found.");
        } else {
            logger.info("Number of inquiries fetched: {}", inquiries.size());
        }

        model.addAttribute("inquiries", inquiries);
        return "admin/inquiry";
    }

    // 문의 답변 작성
    @PostMapping("/inquiry/reply")
    public String replyToInquiry(@RequestParam("inquiryId") int inquiryId,
                                 @RequestParam("replyContent") String replyContent,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            model.addAttribute("username", username);

            Integer adminId = siteUserService.getUserIdByUsername(username);
            model.addAttribute("adminId", adminId);

            inquiryService.saveReply(inquiryId, adminId, replyContent);
        }

        return "redirect:/admin/inquiry";
    }

    // 문의 알람 기능
    @GetMapping("/checkInquiries")
    @ResponseBody
    public boolean checkPendingInquiries() {
        return inquiryService.isInquiryAndReplyCountEqual();
    }

    // 회원 관리 페이지
    @GetMapping("/member")
    public String memberManagement(Model model) {
        logger.info("Accessing member management page.");
        List<SiteUserVO> users = siteUserService.getAllUsers();
        logger.debug("Fetched users: {}", users);

        if (users == null || users.isEmpty()) {
            logger.warn("No users found.");
        } else {
            logger.info("Number of users fetched: {}", users.size());
        }

        model.addAttribute("users", users);
        return "admin/member";
    }

    @PostMapping("/updateUserField")
    @ResponseBody
    public ResponseEntity<String> updateUserField(@RequestParam("username") String username,
                                                  @RequestParam("field") String field,
                                                  @RequestParam("value") String value) {
        try {
            siteUserService.updateUserField(username, field, value);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update field");
        }
    }

    // 재고 관리 페이지
    @GetMapping("/inventory")
    public String inventoryManagement(Model model) {
        List<BookProductVO> products = bookProductService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/inventory";
    }

    // 재고 수량 업데이트 처리
    @PostMapping("/updateProductStock")
    @ResponseBody
    public ResponseEntity<String> updateProductStock(@RequestParam("isbn13") String isbn13,
                                                     @RequestParam("stock") int stock) {
        try {
            bookProductService.updateProductStock(isbn13, stock);
            return ResponseEntity.ok("Stock updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update stock");
        }
    }
}
