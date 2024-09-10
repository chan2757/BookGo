// PaymentController.java
package com.bookgo.controller;

import com.bookgo.mapper.PaymentMapper;
import com.bookgo.service.BookCartService;
import com.bookgo.service.OrderService;
import com.bookgo.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.bookgo.vo.SiteUserVO;
import com.bookgo.mapper.BookCartMapper;
import com.bookgo.service.SiteUserService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PaymentController {




    @Autowired
    private OrderService orderService;

    @Autowired
    private BookCartService bookCartService;

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }



    @Autowired
    private SiteUserService userService; // UserService를 사용하여 사용자 정보를 가져옵니다.

    @Autowired
    private BookCartMapper bookCartMapper;

    @GetMapping("/payment")
    public String showPaymentPage(
            @RequestParam("amount") int amount,
            @RequestParam("items") String items,
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println("Received amount: " + amount);
        System.out.println("Received items: " + items);

        // HttpSession 가져오기
        HttpSession session = request.getSession();

        // 아이템 리스트를 세션에 저장
        session.setAttribute("itemList", items); // JSON 형태의 itemList를 세션에 저장
        // 세션에 저장된 후 출력해 확인
        System.out.println("세션에 저장된 itemList: " + session.getAttribute("itemList"));

        // 로그인된 사용자의 정보를 가져옵니다.
        if (userDetails != null) {
            String username = userDetails.getUsername();
            SiteUserVO user = userService.getUserByUsername(username);

            // 필요한 정보를 Model과 세션에 추가하여 결제 페이지로 전달합니다.
            if (user != null) {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("fullName", user.getFullName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("phone", user.getPhoneNumber());
                model.addAttribute("address", user.getAddress1() + " " + user.getAddress2());
                model.addAttribute("cartId", bookCartMapper.getCartIdByUserId(Math.toIntExact(user.getId())));
                model.addAttribute("totalPrice", amount); // 총 결제 금액을 전달
            }
        }

        return "pay/payment"; // payment.html 페이지를 렌더링합니다.
    }


    // 결제 성공 매핑


    // 결제 취소 매핑
    @GetMapping("/cancel")
    public String paymentCancel() {
        // 결제 취소 페이지로 이동
        return "pay/cancel"; // src/main/resources/templates/pay/cancel.html
    }

    // 결제 실패 매핑
    @GetMapping("/fail")
    public String paymentFail() {
        // 결제 실패 페이지로 이동
        return "pay/fail"; // src/main/resources/templates/pay/fail.html
    }

    // 카카오페이 결제 요청 처리
    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(@RequestBody Map<String, Object> paymentData, HttpServletRequest request) {
        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY DEVCD9F543E05482E85FC8F0AA2650A2734E3323"); // 실제 SECRET_KEY로 변경

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 카카오페이에 POST 요청
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            String tid = (String) responseBody.get("tid"); // 결제 고유 번호 가져오기

            // 세션에 tid 저장
            HttpSession session = request.getSession();
            session.setAttribute("tid", tid);

            // 응답 확인
            System.out.println("Response: " + responseBody);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("결제 요청에 실패했습니다: " + e.getMessage());
        }
    }


    @PostMapping("/storePaymentData")
    public ResponseEntity<?> storePaymentData(@RequestBody Map<String, Object> reqPayData, HttpServletRequest request) {
        // HttpServletRequest를 통해 세션을 가져옵니다.
        HttpSession session = request.getSession();

        // 데이터가 제대로 들어오는지 확인하는 로그
        System.out.println("Received reqPayData: " + reqPayData);

        // reqPayData가 제대로 들어왔는지 확인 후 세션에 저장
        if (reqPayData != null) {
            session.setAttribute("reqPayData", reqPayData);
        } else {
            System.out.println("Error: reqPayData is null");
        }

        return ResponseEntity.ok().build();
    }


    @GetMapping("/success")
    public String paymentSuccess(HttpServletRequest request, Model model) throws JsonProcessingException {
        HttpSession session = request.getSession();

        // 세션에서 데이터 가져오기
        String itemListJson = (String) session.getAttribute("itemList"); // JSON 형태로 가져옴
        Map<String, Object> reqPayData = (Map<String, Object>) session.getAttribute("reqPayData");
        String tid = (String) session.getAttribute("tid"); // 세션에서 tid 가져오기

        // JSON을 객체로 변환
        List<Map<String, Object>> itemList = null;
        if (itemListJson != null) {
            itemList = new ObjectMapper().readValue(itemListJson, new TypeReference<List<Map<String, Object>>>() {});
        }

        System.out.println("세션에서 불러온 itemList: " + itemList);
        System.out.println("세션에서 불러온 reqPayData: " + reqPayData);
        System.out.println("세션에서 불러온 tid: " + tid);

        if (itemList != null && reqPayData != null && tid != null) {
            try {
                // reqPayData에 tid 추가
                reqPayData.put("tid", tid);

                // 결제 성공 시 데이터베이스에 저장
                orderService.saveOrder(reqPayData, itemList);

                String username = (String) reqPayData.get("buyerName"); // reqPayData에서 username 추출
                int userId = userService.getUserIdByUsername(username); // username을 통해 userId 조회

                // itemList를 순회하며 각 항목을 삭제
                for (Map<String, Object> item : itemList) {
                    int cartId = Integer.parseInt(item.get("cartId").toString());
                    String isbn13 = (String) item.get("isbn13");

                    // 장바구니에서 항목 삭제
                    bookCartService.deleteCartItem(cartId, isbn13, userId);

                    System.out.println("삭제된 ISBN13: " + isbn13);
                }

                // 성공 메시지 추가
                model.addAttribute("message", "주문이 성공적으로 저장되었습니다.");
            } catch (Exception e) {
                model.addAttribute("message", "주문 저장 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        } else {
            model.addAttribute("message", "주문 정보를 찾을 수 없습니다.");
        }

        // 세션 데이터 제거
        session.removeAttribute("itemList");
        session.removeAttribute("reqPayData");
        session.removeAttribute("tid");

        return "pay/approval"; // 결제 성공 페이지로 이동
    }


    /**
     * 환불 요청 처리
     *
     * @param requestId    결제 요청 ID
     * @param refundAmount 환불할 금액
     * @return 처리 결과 메시지
     */
    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@RequestParam Long requestId,
                                                @RequestParam int refundAmount,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 1. REQUEST_ID로 TID와 TOTAL_AMOUNT 조회
            Map<String, Object> paymentInfo = paymentService.selectTidAndAmountByRequestId(requestId);
            if (paymentInfo == null || paymentInfo.isEmpty()) {
                throw new Exception("해당 요청 ID로 조회된 결제 정보가 없습니다.");
            }

            String tid = (String) paymentInfo.get("TID");
            BigDecimal totalAmount = (BigDecimal) paymentInfo.get("TOTAL_AMOUNT");

            // 2. 카카오페이 결제 취소 요청
            String apiUrl = "https://open-api.kakaopay.com/online/v1/payment/cancel";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "DEV_SECRET_KEY DEVCD9F543E05482E85FC8F0AA2650A2734E3323");
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> params = new HashMap<>();
            params.put("cid", "TC0ONETIME");
            params.put("tid", tid);
            params.put("cancel_amount", refundAmount);
            params.put("cancel_tax_free_amount", 0);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

            // 요청 성공 여부 확인
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception("결제 취소 요청이 실패했습니다.");
            }

            // 3. 환불 성공 시 DB 처리
            BigDecimal refundAmountDecimal = BigDecimal.valueOf(refundAmount);
            if (refundAmountDecimal.compareTo(totalAmount) == 0) {
                paymentService.deletePaymentRequestByRequestId(requestId);
            } else if (refundAmountDecimal.compareTo(totalAmount) < 0) {
                BigDecimal updatedAmount = totalAmount.subtract(refundAmountDecimal);
                paymentService.updateTotalAmountByRequestId(requestId, updatedAmount);
            } else {
                throw new Exception("환불 금액이 총 결제 금액보다 큽니다.");
            }

            return ResponseEntity.ok("환불이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 처리에 실패했습니다: " + e.getMessage());
        }


    }
}
