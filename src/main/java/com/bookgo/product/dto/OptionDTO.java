package com.bookgo.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
	
	private int optionNo;
	private int refProdNo;
	private int refStockNo;
	private int extraCharge;
	private StockDTO stock;
	private ProductDTO product;
}
