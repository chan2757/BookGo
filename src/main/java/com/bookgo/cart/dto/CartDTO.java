package com.bookgo.cart.dto;

import com.bookgo.product.dto.CategoryDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

	private int cartNo;
	private String memberId;
	private int prodNo;
	private int optionNo;
	private int quantity;
	private ProductDTO product;
	private OptionDTO option;
	private CategoryDTO category;

}
