package com.bookgo.cart.service;

import java.util.List;

import com.bookgo.cart.dto.CartDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

public interface CartService {
	
	List<CartDTO> getCurrentCart(String memberId);
	
	CartDTO getCartItemByOptionNo(String username, int optionNo);

	List<CartDTO> getCartItemList(CartDTO cartDTO);

	OptionDTO searchOptionInfoByOptionNo(int optionNo);

	ProductDTO searchProductInfoByOptionNo(int optionNo);
}