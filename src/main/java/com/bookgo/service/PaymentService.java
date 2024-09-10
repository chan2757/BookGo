package com.bookgo.service;

import com.bookgo.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public void processRefund(Long requestId, Integer refundAmount) throws Exception {
        // 1. REQUEST_ID로 TID와 TOTAL_AMOUNT 조회
        Map<String, Object> paymentInfo = paymentMapper.selectTidAndAmountByRequestId(requestId);
        if (paymentInfo == null || paymentInfo.isEmpty()) {
            throw new Exception("해당 요청 ID로 조회된 결제 정보가 없습니다.");
        }

        String tid = (String) paymentInfo.get("TID");
        BigDecimal totalAmount = (BigDecimal) paymentInfo.get("TOTAL_AMOUNT");

        // 2. TID와 금액으로 결제 취소 요청
        boolean isRefundSuccessful = requestRefund(tid, refundAmount);

        if (!isRefundSuccessful) {
            throw new Exception("결제 취소 요청이 실패했습니다.");
        }

        // 3. 결제 취소 요청 성공 시 처리
        BigDecimal refundAmountDecimal = BigDecimal.valueOf(refundAmount);
        if (refundAmountDecimal.compareTo(totalAmount) == 0) {
            // 입력받은 금액이 TOTAL_AMOUNT와 같다면 해당 결제 요청 삭제
            paymentMapper.deletePaymentRequestByRequestId(requestId);
        } else if (refundAmountDecimal.compareTo(totalAmount) < 0) {
            // 입력받은 금액이 TOTAL_AMOUNT보다 작다면 금액 업데이트
            BigDecimal updatedAmount = totalAmount.subtract(refundAmountDecimal);
            paymentMapper.updateTotalAmountByRequestId(Map.of("requestId", requestId, "totalAmount", updatedAmount));
        } else {
            throw new Exception("환불 금액이 총 결제 금액보다 큽니다.");
        }
    }

    /**
     * 카카오페이 환불 요청 처리 메서드
     *
     * @param tid 결제 고유 번호
     * @param refundAmount 환불 금액
     * @return 환불 성공 여부
     */
    private boolean requestRefund(String tid, Integer refundAmount) {
        // 카카오페이 결제 취소 API URL
        String apiUrl = "https://open-api.kakaopay.com/online/v1/payment/cancel";


        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DEV_SECRET_KEY DEVCD9F543E05482E85FC8F0AA2650A2734E3323");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 파라미터 설정
        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME"); // 상점 코드 (테스트용)
        params.put("tid", tid);
        params.put("cancel_amount", refundAmount);
        params.put("cancel_tax_free_amount", 0); // 비과세 환불 금액 (필요시 설정)

        // 요청 엔터티 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        // REST 템플릿을 사용하여 API 호출
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            // 성공 시 true 반환
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // 예외 발생 시 false 반환
            System.err.println("카카오페이 환불 요청 실패: " + e.getMessage());
            return false;
        }
    }

    // TID와 금액 조회
    public Map<String, Object> selectTidAndAmountByRequestId(Long requestId) {
        return paymentMapper.selectTidAndAmountByRequestId(requestId);
    }

    // 결제 요청 삭제
    public void deletePaymentRequestByRequestId(Long requestId) {
        paymentMapper.deletePaymentRequestByRequestId(requestId);
    }

    // 결제 금액 업데이트
    public void updateTotalAmountByRequestId(Long requestId, BigDecimal amounts) {
        paymentMapper.updateTotalAmountByRequestId(requestId, amounts);
    }
}
