package com.bookgo.mapper;

import com.bookgo.vo.PaymentDetailVO;
import com.bookgo.vo.PaymentRequestVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper {

    // PaymentRequestVO 저장 메서드
    void insertPaymentRequest(PaymentRequestVO paymentRequestVO);

    // PaymentDetailVO 저장 메서드
    void insertPaymentDetail(PaymentDetailVO paymentDetailVO);

    void deletePaymentRequestByRequestId(Long requestId);

    void updateTotalAmountByRequestId(Map<String,? extends Number> requestId);



    void updateTotalAmountByRequestId(Long requestId, BigDecimal amounts);

    Map<String, Object> selectTidAndAmountByRequestId(Long requestId);
}
