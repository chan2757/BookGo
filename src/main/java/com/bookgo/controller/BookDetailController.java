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

        System.out.println(bookDetail.toString());

        return "bookgo/bookDetail"; // bookDetail.html 페이지로 이동
    }

    // GET 매핑 추가: 제목 클릭 시 책 상세 페이지로 이동
    @GetMapping
    public String showBookDetail(@RequestParam String isbn13, Model model) {
        logger.info("Fetching details for ISBN: {}", isbn13);

        // ISBN13을 사용하여 책 상세 정보 가져오기
        BookDetailVO bookDetail = bookDetailService.getBookDetail(isbn13, new BookDpVO());
        model.addAttribute("bookDetail", bookDetail);

        return "bookgo/bookDetail"; // bookDetail.html 페이지로 이동
    }
}
