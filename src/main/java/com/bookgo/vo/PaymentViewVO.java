    package com.bookgo.vo;

    import lombok.Data;

    import java.sql.Timestamp;
    import java.util.List;

    @Data
    public class PaymentViewVO {
        private Long requestId;       // 결제 요청 ID
        private String orderId;       // 주문 ID
        private String username;      // 사용자 이름
        private int totalAmount;   // 총 결제 금액
        private Timestamp createdAt;  // 결제 생성일
        private String fullName;      // 사용자 이름
        private String email;         // 이메일
        private String phone;         // 전화번호
        private String address;       // 주소
        private List<PaymentProductVO> products; // 결제된 상품 목록
    }

