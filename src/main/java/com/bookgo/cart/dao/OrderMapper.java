package com.bookgo.cart.dao;

import org.apache.ibatis.annotations.Mapper;

import com.bookgo.cart.dto.DeliveryDTO;
import com.bookgo.cart.dto.OrderDTO;
import com.bookgo.cart.dto.OrderDetailDTO;
import com.bookgo.cart.dto.PaymentDTO;
import com.bookgo.cart.dto.PointDTO;
import com.bookgo.product.dto.OptionDTO;

@Mapper
public interface OrderMapper {

	int addOrderInfo(OrderDTO orderDTO);
	
	int decreaseStockAmount(int orderQuantity, int optionNo);

	int addOrderDetail(OrderDetailDTO orderDetailDTO);

	int addPaymentInfo(PaymentDTO paymentDTO);

	OptionDTO getWishItemByOptionNo(String username, int optionNo);

	int addDeliveryInfo(DeliveryDTO deliveryDTO);

	int addPointInfo(PointDTO pointDTO);

	int getTheNumberOfEachOrder(String orderNo);
}