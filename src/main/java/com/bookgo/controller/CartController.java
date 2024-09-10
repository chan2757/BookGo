package com.bookgo.controller;

import com.bookgo.service.BookCartService;
import com.bookgo.vo.BookCartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private BookCartService bookCartService;

    // 장바구니에 아이템 추가
    @PostMapping("/add")
    public String addItemToCart(@RequestBody BookCartItemVO item) {
        bookCartService.addItem(item);
        return "장바구니에 추가되었습니다.";
    }

    // 장바구니 아이템 조회
    @GetMapping("/items")
    public List<BookCartItemVO> getCartItems() {
        return bookCartService.getCartItems();
    }
}
