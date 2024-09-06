
package com.bookgo.review.dao;

import org.apache.ibatis.annotations.Mapper;

import com.bookgo.cart.dto.PointDTO;
import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.review.dto.ReviewDTO;

@Mapper
public interface ReviewMapper {

	int checkCurrReviewNo();

	int attachReviewImages(AttachmentDTO attachment);

	int postAReview(ReviewDTO reviewDTO);
	
	String getPaymentNoByOrderNo(String orderNo);

	int savePoints(PointDTO pointDTO);

	int incrementReviewViewCount(int reviewNo);

	String getAttachmentByReviewNo(int reviewNo, int num);
	
	ReviewDTO getReviewDetails(int reviewNo);

	Integer checkReviewNoToEdit(String memberId, String orderNo, int optionNo);

	int updateAReview(ReviewDTO reviewDTO);

	int checkAttachedFiles(int reviewNo);
	
	void deleteAttachedFiles(int reviewNo);

	void deleteDownloadHits(int reviewNo);
}
