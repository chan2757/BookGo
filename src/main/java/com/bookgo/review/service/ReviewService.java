package com.bookgo.review.service;

import com.bookgo.cart.dto.PointDTO;
import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.review.dto.ReviewDTO;

public interface ReviewService {
	
	int checkCurrReviewNo();
	
	int attachReviewImages(AttachmentDTO attachment);
	
	int postAReview(ReviewDTO reviewDTO);
	
	String getPaymentNoByOrderNo(String orderNo);
	
	int savePoints(PointDTO pointDTO);
	
	int incrementReviewViewCount(int reviewNo);
	
	String getAttachmentByReviewNo(int reviewNo, int num);
	
	ReviewDTO getReviewDetails(int reviewNo);
	
	int checkReviewNoToEdit(String memberId, String orderNo, int optionNo);
	
	int updateAReview(ReviewDTO reviewDTO);
}
