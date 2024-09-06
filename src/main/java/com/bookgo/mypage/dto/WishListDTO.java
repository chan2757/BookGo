package com.bookgo.mypage.dto;

import java.util.List;

import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.product.dto.BrandDTO;
import com.bookgo.product.dto.CategoryDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

import lombok.Data;

@Data
public class WishListDTO {

	private String memberId;
	private int optionNo;
	private ProductDTO product;
	private OptionDTO option;
	private CategoryDTO category;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}