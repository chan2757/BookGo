package com.bookgo.cart.dto;

import java.util.List;

import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.product.dto.BrandDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

import lombok.Data;

@Data
public class OrderDTO {

	private String orderNo;						// 주문번호
	private int optionNo;						// 옵션번호
	private int orderQuantity;					// 상품별 주문 수량
	private int orderAmount;					// 상품 총 주문 수량
	private OrderDetailDTO orderDetail;			// 오더 상세정보 dto
	private DeliveryDTO delivery;				
	private OptionDTO option;					
	private ProductDTO product;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}
