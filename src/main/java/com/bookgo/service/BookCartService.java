package com.bookgo.service;

import com.bookgo.controller.BookCartController;
import com.bookgo.mapper.BookCartMapper;
import com.bookgo.vo.BookCartItemVO;
import com.bookgo.vo.CartDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookCartService {

    private static final Logger logger = LoggerFactory.getLogger(BookCartController.class);

    @Autowired
    private BookCartMapper bookCartMapper;

    /**
     * 장바구니에 책을 추가하는 로직
     *
     * @param userId   로그인된 유저의 ID
     * @param isbn13   추가할 책의 ISBN13
     * @param quantity 추가할 수량
     */
    @Transactional
    public void addToCart(int userId, String isbn13, int quantity) {
        // 유저의 카트 아이디 조회
        Integer cartId = bookCartMapper.getCartIdByUserId(userId);

        // 카트가 존재하지 않으면 생성
        if (cartId == null) {
            bookCartMapper.insertBookCart(userId);
            cartId = bookCartMapper.getCartIdByUserId(userId); // 새로 생성된 카트 아이디 조회
        }

        // 카트 아이디와 ISBN으로 카트 아이템 조회
        BookCartItemVO existingItem = bookCartMapper.getCartItemByCartIdAndIsbn13(cartId, isbn13);

        // 아이템이 존재하면 수량을 업데이트
        if (existingItem != null) {
            bookCartMapper.updateCartItemQuantity(cartId, isbn13, existingItem.getQuantity() + quantity);
        } else {
            // 아이템이 없으면 새로 추가
            BookCartItemVO newItem = new BookCartItemVO();
            newItem.setCartId(cartId);
            newItem.setIsbn13(isbn13);
            newItem.setQuantity(quantity);
            bookCartMapper.insertCartItem(newItem);
        }
    }

    /**
     * 로그인된 사용자의 카트 상세 정보 조회
     *
     * @param userId 로그인된 사용자의 ID
     * @return 장바구니 상세 정보 리스트
     */
    public List<CartDetailVO> getCartDetails(int userId) {
        logger.info("Retrieving cart details for userId: {}", userId);

        // 사용자 ID로 카트 ID 조회
        Integer cartId = bookCartMapper.getCartIdByUserId(userId);
        logger.info("Retrieved cartId: {}", cartId);

        // 카트가 없으면 null 반환
        if (cartId == null) {
            logger.info("No cart found for userId: {}", userId);
            return null;
        }

        // 카트 ID로 장바구니 상세 정보 조회
        List<CartDetailVO> cartDetails = bookCartMapper.getCartDetailsByCartId(cartId);

        logger.info("77777Retrieved cartDetails: {}", cartDetails);


        if (cartDetails == null || cartDetails.isEmpty()) {
            logger.info("No cart items found for cartId: {}", cartId);
            return null;
        }

        logger.info("Retrieved cart details: {}", cartDetails);
        return cartDetails;
    }

    // BookCartService.java
    public void updateCartItemQuantity(int cartId, String isbn13, int quantity) {
        Map<String, Object> params = new HashMap<>();
        params.put("cartId", cartId);
        params.put("isbn13", isbn13);
        params.put("quantity", quantity);

        // 변경된 매퍼 메서드 호출
        bookCartMapper.updateCartItemQuantitywith(params);
    }
}
