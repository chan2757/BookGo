package com.bookgo.cart.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bookgo.cart.dto.CartDTO;
import com.bookgo.product.dto.BrandDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;


@Mapper
public interface CartMapper {
	
	List<CartDTO> getCurrentCart(String username);
	
	CartDTO getCartItemByOptionNo(String username, int optionNo);
	
	int getMemberCartNo(String username);

	List<CartDTO> getCartItemList(CartDTO cartDTO);

	void addToCart(CartDTO cartDTO);

	void updateQuantityInCart(String memberId, int quantity, int optionNo);
	
	void deleteItemFromCart(String memberId, int optionNo);

	OptionDTO searchOptionInfoByOptionNo(int optionNo);

	ProductDTO searchProductInfoByOptionNo(int optionNo);
	
	BrandDTO searchBrandInfoByOptionNo(int optionNo);
}