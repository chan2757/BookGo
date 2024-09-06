package com.bookgo.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bookgo.cart.dto.OrderDTO;
import com.bookgo.cart.dto.OrderDetailDTO;
import com.bookgo.cart.dto.PointDTO;
import com.bookgo.mypage.dto.MemberDTO;
import com.bookgo.mypage.dto.WishListDTO;
import com.bookgo.review.dto.ReviewDTO;

@Mapper
public interface MemberMapper {

	/* 마이페이지 */
	void addToWishList(String memberId, int optionNo);

	List<WishListDTO> getMemberWishList(String memberId);
	
	List<Integer> getProdNoFromWishList(String memberId);

	int deleteItemFromWishList(String memberId, int optionNo);
	
	List<OrderDetailDTO> getMemberOrderList(String memberId);
	
	OrderDetailDTO getMemberOrderDetails(String memberId, String orderNo);

	List<PointDTO> getReserveDetails(String memberId);

	List<OrderDTO> getOptionListByOrderNo(String orderNo);

	int getTotalOrderAmountByOrderNo(String orderNo);

	List<OrderDTO> getItemsToPostAReview(String memberId);

	OrderDTO getOrderInfoToReview(String memberId, String orderNo, int optionNo);

	List<ReviewDTO> getMemberReviewPosts(String memberId);

	int getMemberPoint(String memberId);

	int getMemberOrderCountByDlvrStatus(String memberId, String dlvrStatus);
	
	MemberDTO getMemberDetails(String memberId);

	char checkIsAuthenticated(String memberId);

	int updateAuthentication(String memberId, char authPhoneYn);

	int changeMemberInfo(MemberDTO memberDTO);
	
	int closeMemberAccount(String memberId);
	

	/* 랜덤 회원 추출 */
	MemberDTO getRandomMember();
}
