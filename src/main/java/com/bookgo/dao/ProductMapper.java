package com.bookgo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bookgo.paging.dto.Criteria;
import com.bookgo.paging.dto.ItemCriteria;
import com.bookgo.product.dto.AttachmentDTO;
import com.bookgo.product.dto.BrandDTO;
import com.bookgo.product.dto.CategoryDTO;
import com.bookgo.product.dto.OptionDTO;
import com.bookgo.product.dto.ProductDTO;
import com.bookgo.review.dto.ReviewDTO;

@Mapper
public interface ProductMapper {

	List<CategoryDTO> getCategoryList();
	
	int checkCategoryNo(String categoryName);

	int checkCategoryName(String categoryName);

	int addANewCategory(String categoryName, String categorySection);

	List<BrandDTO> getBrandList();
	
	int checkBrandNo(String brandName);
	
	int checkBrandName(String brandName);
	
	int addNewBrand(String brandName);
	
	int addProductStock(int stockAmount);

	int addProductOption(String bodyColor, String inkColor, double pointSize, int extraCharge);

	int addProduct(int categoryNo, String prodName, String prodDesc, String productTag, int discountRate,
			int prodPrice, int brandNo, String prodOrigin, String prodDetailContent);

	int attachProdThumbnail(AttachmentDTO attachment);

	int attachProdContentImage(AttachmentDTO attachment);
	
	int checkCurrProdNo();
	
	int checkNextProdNo();

	void updateProdNoContentImage(String savePath);

	int incrementProdDetailViewCount(int prodNo);

	ProductDTO getProductDetails(int prodNo);

	int getTotalNumber(Criteria criteria);
	
	int getOnSaleNumber(Criteria criteria);
	
	int getStopSaleNumber(Criteria criteria);
	
	int getOnDiscountNumber(Criteria criteria);
	
	int getSoldOutNumber(Criteria criteria);

	List<ProductDTO> getProductList(Criteria criteria);
	
	List<ProductDTO> getOnSaleOnly(Criteria criteria);
	
	List<ProductDTO> getStopSaleOnly(Criteria criteria);
	
	List<ProductDTO> getOnDiscountOnly(Criteria criteria);
	
	List<ProductDTO> getSoldOutOnly(Criteria criteria);

	AttachmentDTO getMainThumbnailByProdNo(int prodNo);
	
	AttachmentDTO getSubThumbnailByProdNo(int prodNo);

	List<OptionDTO> getOptionListByProdNo(int prodNo);

	String searchSaleYnByProdNo(int prodNo);

	int stopSellingAProduct(int prodNo);

	int putAProductOnSale(int prodNo);
	
	void deleteOption(int prodNo);
	
	void deleteOptionStock();
	
	void deleteAttachment(int prodNo);

	int deleteProduct(int prodNo);

	int searchProdNoByOptionNo(int optionNo);

	List<ProductDTO> getProductListByCategorySection(ItemCriteria itemCriteria);

	List<String> getCategorySection();

	List<String> getCategoryListBySection(String category);

	int getTotalNumberByCriteria(ItemCriteria itemCriteria);

	Integer getSalesByProdNo(int prodNo);

	CategoryDTO getCategoryByProdNo(int prodNo);

	BrandDTO getBrandByProdNo(int prodNo);

	List<ReviewDTO> getReviewListByProdNo(int prodNo);

	double averageReviewRating(int prodNo);

	int getPercentageOfRating(int total, int prodNo, int star);

	int getNumberOfRatings(int prodNo, int star);

	int getTotalNumberOfReviews(int prodNo);

	List<AttachmentDTO> getAttachmentByReviewNo(int reviewNo);

	List<String> getBrandNameBySection(String category);

	int getTotalNumberByMinorCategory(String category);

	List<ProductDTO> searchProductByKeyword(String keyword);

	List<Integer> getAllProdNo();
	
	OptionDTO getRandomProductOption(int number);

	List<ProductDTO> getProductsToDisplay();
}
