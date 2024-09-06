package com.bookgo.cart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookgo.cart.dao.CartMapper;
import com.bookgo.cart.dto.CartDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;

@Service("cartService")
public class CartServiceImpl implements CartService {

	private CartMapper cartMapper;
	
	@Autowired
	public CartServiceImpl(CartMapper cartMapper) {
		this.cartMapper = cartMapper;
	}
	
	@Override
	public List<CartDTO> getCurrentCart(String memberId) {
		return cartMapper.getCurrentCart(memberId);
	}
	
	@Override
	public CartDTO getCartItemByOptionNo(String username, int optionNo) {
		return cartMapper.getCartItemByOptionNo(username, optionNo);
	}

	@Override
	public List<CartDTO> getCartItemList(CartDTO cartDTO) {
		return cartMapper.getCartItemList(cartDTO);
	}

	@Override
	public OptionDTO searchOptionInfoByOptionNo(int optionNo) {
		return cartMapper.searchOptionInfoByOptionNo(optionNo);
	}

	@Override
	public ProductDTO searchProductInfoByOptionNo(int optionNo) {
		return cartMapper.searchProductInfoByOptionNo(optionNo);
	}

}