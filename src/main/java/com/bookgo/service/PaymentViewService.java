package com.bookgo.service;

import com.bookgo.mapper.PaymentViewMapper;
import com.bookgo.vo.PaymentViewVO;
import com.bookgo.vo.PaymentProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentViewService {

    private final PaymentViewMapper paymentViewMapper;

    @Autowired
    public PaymentViewService(PaymentViewMapper paymentViewMapper) {
        this.paymentViewMapper = paymentViewMapper;
    }

    // 사용자의 결제 요청 목록을 조회
    public List<PaymentViewVO> getPaymentRequestsByUsername(String username) {
        return paymentViewMapper.selectPaymentRequestsByUsername(username);
    }

    // 특정 결제 요청의 상세 내역을 조회
    public List<PaymentProductVO> getPaymentDetailsByRequestId(Long requestId) {
        return paymentViewMapper.selectPaymentDetailsByRequestId(requestId);
    }

    public List<PaymentViewVO> getAllPaymentRequests() {
        return paymentViewMapper.selectPaymentRequests();
    }
}
