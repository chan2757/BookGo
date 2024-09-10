package com.bookgo.mapper;

import com.bookgo.vo.BookCartItemVO;
import com.bookgo.vo.CartDetailVO; // 새로 추가된 VO 클래스
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookCartMapper {

    /**
     * 유저 아이디로 카트 번호 조회
     *
     * @param userId 유저 아이디
     * @return 해당 유저의 카트 아이디
     */
    Integer getCartIdByUserId(@Param("userId") int userId);

    /**
     * 유저 아이디로 카트 생성
     *
     * @param userId 유저 아이디
     */
    void insertBookCart(@Param("userId") int userId);

    /**
     * 카트 아이디와 ISBN13으로 카트 아이템 조회
     *
     * @param cartId 카트 아이디
     * @param isbn13 책의 ISBN13
     * @return 조회된 카트 아이템 정보
     */
    BookCartItemVO getCartItemByCartIdAndIsbn13(@Param("cartId") int cartId, @Param("isbn13") String isbn13);

    /**
     * 카트 아이템 수량 업데이트
     *
     * @param cartId  카트 아이디
     * @param isbn13  책의 ISBN13
     * @param quantity 추가할 수량
     */
    void updateCartItemQuantity(@Param("cartId") int cartId, @Param("isbn13") String isbn13, @Param("quantity") int quantity);

    /**
     * 새로운 카트 아이템 추가
     *
     * @param cartItemVO 추가할 카트 아이템 정보
     */
    void insertCartItem(BookCartItemVO cartItemVO);

    /**
     * 카트 아이디로 카트 상세 정보 조회
     *
     * @param cartId 카트 아이디
     * @return 카트 상세 정보 리스트
     */
    List<CartDetailVO> getCartDetailsByCartId(@Param("cartId") int cartId);

    void updateCartItemQuantitywith(Map<String, Object> params);
}
