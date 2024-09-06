package com.bookgo.cart.dto;

import java.util.List;

import com.bookgo.mypage.dto.MemberDTO;
import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.product.dto.BrandDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

import lombok.Data;

@Data
public class OrderDetailDTO {

	private String orderNo;
	private String memberId;
	private String orderDate;
	private String rcvrName;
	private String rcvrPhone;
	private String rcvrAddress;
	private String dlvrReqMessage;
	private String dlvrStatus;
	private OrderDTO order;
	private PaymentDTO payment;
	private DeliveryDTO delivery;
	private PointDTO point;
	private OptionDTO option;
	private ProductDTO product;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
	private MemberDTO member;
}