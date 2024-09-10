package com.bookgo.service;

import com.bookgo.mapper.PaymentMapper;
import com.bookgo.vo.PaymentDetailVO;
import com.bookgo.vo.PaymentRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private PaymentMapper paymentMapper;

    // 결제 요청과 상세 항목을 저장하는 메서드
    public void saveOrder(Map<String, Object> reqPayData, List<Map<String, Object>> itemList) {
        // PaymentRequestVO 생성 및 설정
        PaymentRequestVO paymentRequestVO = new PaymentRequestVO();
        paymentRequestVO.setOrderId((String) reqPayData.get("orderId"));
        paymentRequestVO.setUsername((String) reqPayData.get("buyerName"));
        paymentRequestVO.setFullName((String) reqPayData.get("buyerFName"));
        paymentRequestVO.setEmail((String) reqPayData.get("buyerEmail"));
        paymentRequestVO.setPhone((String) reqPayData.get("buyerTel"));
        paymentRequestVO.setAddress((String) reqPayData.get("buyerAddr"));
        paymentRequestVO.setTotalAmount((Integer) reqPayData.get("requestPrice"));
        paymentRequestVO.setTid((String) reqPayData.get("tid"));

        // PaymentRequestVO 출력
        System.out.println("PaymentRequestVO: " + paymentRequestVO);

        // PaymentRequestVO 저장
        paymentMapper.insertPaymentRequest(paymentRequestVO);

        // itemList의 각 항목을 PaymentDetailVO로 변환하여 저장
        for (Map<String, Object> item : itemList) {
            PaymentDetailVO paymentDetailVO = new PaymentDetailVO();
            paymentDetailVO.setRequestId(paymentRequestVO.getOrderId());
            paymentDetailVO.setProductName((String) item.get("title"));
            paymentDetailVO.setIsbn13((String) item.get("isbn13"));
            paymentDetailVO.setQuantity((Integer) item.get("quantity"));
            paymentDetailVO.setItemPrice((Integer) item.get("price"));
            paymentDetailVO.setTotalPrice(paymentDetailVO.getQuantity() * paymentDetailVO.getItemPrice());

            // PaymentDetailVO 출력
            System.out.println("PaymentDetailVO: " + paymentDetailVO);

            // PaymentDetailVO 저장
            paymentMapper.insertPaymentDetail(paymentDetailVO);
        }
    }

    // TransactionData 저장 메서드
    public void saveTransactionData(String tid, Map<String, Object> paymentData) {
        PaymentRequestVO paymentRequestVO = new PaymentRequestVO();
        paymentRequestVO.setOrderId((String) paymentData.get("partner_order_id"));
        paymentRequestVO.setTotalAmount((Integer) paymentData.get("total_amount"));
        paymentRequestVO.setTid(tid);

        // PaymentRequestVO 출력
        System.out.println("Transaction PaymentRequestVO: " + paymentRequestVO);

        // PaymentRequestVO 저장
        paymentMapper.insertPaymentRequest(paymentRequestVO);
    }
}
