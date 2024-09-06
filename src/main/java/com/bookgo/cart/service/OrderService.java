package com.bookgo.cart.service;


import com.bookgo.cart.dto.DeliveryDTO;
import com.bookgo.cart.dto.OrderDetailDTO;
import com.bookgo.cart.dto.PaymentDTO;
import com.bookgo.cart.dto.PointDTO;
import com.bookgo.product.dto.OptionDTO;

import net.sf.json.JSONArray;



public interface OrderService {

	boolean orderAndPay(OrderDetailDTO orderDetailDTO, JSONArray optionNoArr, JSONArray optionQtArr,
			JSONArray orderPriceArr, DeliveryDTO deliveryDTO, PointDTO pointDTO, PaymentDTO paymentDTO);

	OptionDTO getWishItemByOptionNo(String username, int optionNo);

	int getTheNumberOfEachOrder(String orderNo);
}