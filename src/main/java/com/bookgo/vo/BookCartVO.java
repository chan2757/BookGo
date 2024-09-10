package com.bookgo.vo;

import lombok.Data;

@Data
public class BookCartVO {
    private int cartId;        // 장바구니 고유 ID
    private int userId;        // 사용자 ID (site_user 테이블의 ID)
}
