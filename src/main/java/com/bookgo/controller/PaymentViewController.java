    package com.bookgo.controller;

    import com.bookgo.service.PaymentViewService;
    import com.bookgo.vo.PaymentViewVO;
    import com.bookgo.vo.PaymentProductVO;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;

    import java.util.List;

    @Controller
    @RequestMapping("/mypayment")
    public class PaymentViewController {

        private final PaymentViewService paymentViewService;

        public PaymentViewController(PaymentViewService paymentViewService) {
            this.paymentViewService = paymentViewService;
        }

        // 사용자의 결제 요청 목록을 조회하여 화면에 표시
        @GetMapping("/list")
        public String getPaymentRequests(Model model, @AuthenticationPrincipal UserDetails userDetails) {
            if (userDetails != null) {
                String username = userDetails.getUsername();
                List<PaymentViewVO> paymentRequests = paymentViewService.getPaymentRequestsByUsername(username);

                // 결제 내역이 없는 경우에도 오류가 발생하지 않도록 처리
                if (paymentRequests == null || paymentRequests.isEmpty()) {
                    model.addAttribute("message", "결제 내역이 없습니다.");
                } else {
                    model.addAttribute("paymentRequests", paymentRequests);
                }
            } else {
                model.addAttribute("message", "로그인이 필요합니다.");
                return "redirect:/user/login";
            }
            return "myown/mypayment";
        }

        // 특정 결제 요청의 상세 내역을 조회하여 화면에 표시
        @GetMapping("/detail/{requestId}")
        public String getPaymentDetails(@PathVariable Long requestId, Model model) {
            List<PaymentProductVO> paymentDetails = paymentViewService.getPaymentDetailsByRequestId(requestId);

            // 결제 상세 내역이 없는 경우에도 오류가 발생하지 않도록 처리
            if (paymentDetails == null || paymentDetails.isEmpty()) {
                model.addAttribute("message", "결제 상세 내역이 없습니다.");
            } else {
                model.addAttribute("paymentDetails", paymentDetails);
            }
            return "myown/paymentDetail";
        }
    }
