package com.bookgo.controller;

import com.bookgo.service.BookDetailService;
import com.bookgo.vo.BookDetailVO;
import com.bookgo.vo.BookDpVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookgo/book/detail")
public class BookDetailController {

    private static final Logger logger = LoggerFactory.getLogger(BookDetailController.class);

    private final BookDetailService bookDetailService;

    public BookDetailController(BookDetailService bookDetailService) {
        this.bookDetailService = bookDetailService;
    }



    // POST 매핑: 생성된 BookDpVO로 도서 상세 정보를 가져오는 메서드
    @PostMapping("/view")
    public String getBookDetail(@ModelAttribute BookDpVO dp, Model model) {
        logger.info("Received BookDpVO: {}", dp);
        // BookDetailService를 통해 책 상세 정보 가져오기
        BookDetailVO bookDetail = bookDetailService.getBookDetail( dp);
        model.addAttribute("bookDetail", bookDetail);
        return "bookgo/bookDetail"; // bookDetail.html 페이지로 이동
    }
}
