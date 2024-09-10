package com.bookgo.controller;

import com.bookgo.service.BookDetailService;
import com.bookgo.vo.BookDpVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BookIndexController {

    private static final Logger logger = LoggerFactory.getLogger(BookDetailController.class);
    private final BookDetailService bookDetailService;

    public BookIndexController(BookDetailService bookDetailService) {
        this.bookDetailService = bookDetailService;
    }

    // /bookgo/index와 /index 매핑을 모두 처리
    @GetMapping({"/bookgo/index", "/index"})
    public String indexPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        logger.info("Accessed index page");

        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
            logger.info("Logged in user: " + userDetails.getUsername());
        }

        List<BookDpVO> bestsellerBooks = bookDetailService.makeDpList("Bestseller");
        List<BookDpVO> newAllBooks = bookDetailService.makeDpList("ItemNewAll");
        List<BookDpVO> newSpecialBooks = bookDetailService.makeDpList("ItemNewSpecial");

        model.addAttribute("bestsellerBooks", bestsellerBooks);
        model.addAttribute("newAllBooks", newAllBooks);
        model.addAttribute("newSpecialBooks", newSpecialBooks);

        logger.info("Returning view: bookgo/index");

        return "bookgo/index";
    }
}
