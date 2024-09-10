package com.bookgo.controller;

import com.bookgo.service.BookCartService;
import com.bookgo.service.SiteUserService;
import com.bookgo.vo.CartDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class BookCartController {

    private static final Logger logger = LoggerFactory.getLogger(BookCartController.class);

    @Autowired
    private BookCartService bookCartService;

    @Autowired
    private SiteUserService siteUserService;

    /**
     * 장바구니에 책을 추가하는 API
     *
     * @param request   추가할 책의 정보 (isbn13, quantity)
     * @param principal 로그인된 유저 정보
     * @return 추가 결과를 반환하는 메시지
     */
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestBody Map<String, Object> request, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = principal.getName();
            logger.info("Logged in username: {}", username);

            int userId = siteUserService.getUserIdByUsername(username);
            logger.info("Retrieved userId: {}", userId);

            String isbn13 = (String) request.get("isbn13");
            int quantity = (int) request.get("quantity");
            logger.info("Received request - ISBN13: {}, Quantity: {}", isbn13, quantity);

            bookCartService.addToCart(userId, isbn13, quantity);

            response.put("success", true);
            response.put("message", "책이 장바구니에 추가되었습니다.");
        } catch (Exception e) {
            logger.error("Error adding to cart: ", e);
            response.put("success", false);
            response.put("message", "장바구니에 추가하는 중 오류가 발생했습니다.");
        }

        return response;
    }

    /**
     * 장바구니 상세 페이지를 보여주는 메서드
     *
     * @param principal 로그인된 사용자 정보
     * @param model     모델 객체
     * @return 장바구니 상세 페이지
     */
    @GetMapping("/detail")
    public String showCartDetail(Principal principal, Model model) {
        String username = principal.getName();
        logger.info("Retrieving cart details for username: {}", username);

        int userId = siteUserService.getUserIdByUsername(username);
        logger.info("Retrieved userId: {}", userId);

        List<CartDetailVO> cartDetails = bookCartService.getCartDetails(userId);

        System.out.println("cartDetails: " + cartDetails);

        logger.info("Retrieved cartDetails: {}", cartDetails);

        if (cartDetails == null || cartDetails.isEmpty()) {
            logger.info("No cart items found for userId: {}", userId);
            model.addAttribute("cartDetails", Collections.emptyList()); // 빈 리스트로 설정
            model.addAttribute("message", "장바구니에 담긴 상품이 없습니다.");
        } else {
            model.addAttribute("cartDetails", cartDetails);
        }

        return "cart/detail";
    }

    /**
     * 장바구니 항목 수량 업데이트 API
     *
     * @param request   업데이트할 장바구니 항목 정보 (cartId, isbn13, quantity)
     * @param principal 로그인된 유저 정보
     * @return 업데이트 결과를 반환하는 메시지
     */
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateCartItem(@RequestBody Map<String, Object> request, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = principal.getName();
            logger.info("Logged in username: {}", username);

            int userId = siteUserService.getUserIdByUsername(username);
            logger.info("Retrieved userId: {}", userId);

            // JSON에서 전달된 값이 String으로 되어 있다면 parse를 통해 int로 변환
            int cartId = Integer.parseInt(request.get("cartId").toString());
            String isbn13 = (String) request.get("isbn13");
            int quantity = Integer.parseInt(request.get("quantity").toString());

            logger.info("Updating cart item - Cart ID: {}, ISBN13: {}, Quantity: {}", cartId, isbn13, quantity);

            // 서비스 메서드 호출하여 수량 업데이트
            bookCartService.updateCartItemQuantity(cartId, isbn13, quantity);

            response.put("success", true);
            response.put("message", "장바구니 항목 수량이 업데이트되었습니다.");
        } catch (Exception e) {
            logger.error("Error updating cart item: ", e);
            response.put("success", false);
            response.put("message", "장바구니 항목 업데이트 중 오류가 발생했습니다.");
        }

        return response;
    }
    /**
     * 장바구니 항목 삭제 API
     *
     * @param request   삭제할 장바구니 항목 정보 (cartId, isbn13)
     * @param principal 로그인된 유저 정보
     * @return 삭제 결과를 반환하는 메시지
     */
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> deleteCartItem(@RequestBody Map<String, Object> request, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = principal.getName();
            logger.info("Logged in username: {}", username);

            int userId = siteUserService.getUserIdByUsername(username);
            logger.info("Retrieved userId: {}", userId);

            // JSON에서 전달된 값이 String으로 되어 있다면 parse를 통해 int로 변환
            int cartId = Integer.parseInt(request.get("cartId").toString());
            String isbn13 = (String) request.get("isbn13");

            logger.info("Deleting cart item - Cart ID: {}, ISBN13: {}", cartId, isbn13);

            // 장바구니 항목 삭제
            bookCartService.deleteCartItem(cartId, isbn13, userId);

            response.put("success", true);
            response.put("message", "장바구니 항목이 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("Error deleting cart item: ", e);
            response.put("success", false);
            response.put("message", "장바구니 항목 삭제 중 오류가 발생했습니다.");
        }

        return response;
    }
}
