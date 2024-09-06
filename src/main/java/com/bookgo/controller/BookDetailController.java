package com.bookgo.controller;

import com.bookgo.service.BookDetailService;
import com.bookgo.vo.BookDetailVO;
import com.bookgo.vo.BookDpVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookgo/book/detail")
public class BookDetailController {

    private static final Logger logger = LoggerFactory.getLogger(BookDetailController.class);

    private final BookDetailService bookDetailService;

    // BookDetailService 의존성 주입
    public BookDetailController(BookDetailService bookDetailService) {
        this.bookDetailService = bookDetailService;
    }

    @PostMapping
    public String getBookDetail(@ModelAttribute BookDpVO dp, Model model) {

        logger.info("Received BookDpVO: {}", dp);
        // BookDetailService를 통해 책 상세 정보 가져오기
        BookDetailVO bookDetail = bookDetailService.getBookDetail(dp.getIsbn13(), dp);

        model.addAttribute("bookDetail", bookDetail);
        return "bookgo/bookDetail"; // bookDetail.html 페이지로 이동
    }
}
