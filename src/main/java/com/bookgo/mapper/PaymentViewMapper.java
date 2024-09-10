package com.bookgo.mapper;

import com.bookgo.vo.PaymentViewVO;
import com.bookgo.vo.PaymentProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentViewMapper {

    // 특정 사용자에 대한 결제 요청 목록을 조회
    List<PaymentViewVO> selectPaymentRequestsByUsername(@Param("username") String username);

    // 특정 요청 ID에 대한 결제 상품 목록을 조회
    List<PaymentProductVO> selectPaymentDetailsByRequestId(@Param("requestId") Long requestId);


    List<PaymentViewVO> selectPaymentRequests();

    void updateTotalAmountByRequestId(Map<String, Object> params);
}
