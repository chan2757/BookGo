package com.bookgo.review.dto;

import java.util.Date;
import java.util.List;

import com.bookgo.cart.dto.DeliveryDTO;
import com.bookgo.cart.dto.OrderDTO;
import com.bookgo.cart.dto.OrderDetailDTO;
import com.bookgo.mypage.dto.MemberDTO;
import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

import lombok.Data;

@Data
public class ReviewDTO {

	private int reviewNo;
	private int optionNo;
	private String orderNo;
	private String memberId;
	private String revwTitle;
	private String revwContent;
	private Date revwRegDate;
	private int revwHits;
	private int revwRatings;
	private OptionDTO option;
	private ProductDTO product;
	private OrderDTO order;
	private OrderDetailDTO orderDetail;
	private DeliveryDTO delivery;
	private MemberDTO member;
	private List<AttachmentDTO> attachmentList;
}
