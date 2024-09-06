package com.bookgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping({"/bookgo/books", "/bookgo/book"})
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private static final String ALADIN_API_KEY = "ttbyyd0000939001";
    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/";

    @GetMapping("/list")
    public String getBookList(@RequestParam(defaultValue = "Bestseller") String queryType,
                              @RequestParam(defaultValue = "1") int page, Model model) {
        logger.info("도서 리스트 요청: {} 페이지: {}", queryType, page);

        int maxResults = 20;
        int startIndex = (page - 1) * maxResults + 1;

        String apiUrl = BASE_URL + "ItemList.aspx?ttbkey=" + ALADIN_API_KEY +
                "&QueryType=" + queryType + "&MaxResults=" + maxResults +
                "&start=" + startIndex + "&SearchTarget=Book&output=js&Version=20131101";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        model.addAttribute("books", response != null ? response.get("item") : null);
        model.addAttribute("queryType", queryType);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 10);

        return "bookgo/bookList";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam String query,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "Title") String queryType, Model model) {
        logger.info("도서 검색 요청: {}, 페이지: {}", query, page);

        int start = (page - 1) * 20 + 1;
        String apiUrl = BASE_URL + "ItemSearch.aspx?ttbkey=" + ALADIN_API_KEY +
                "&Query=" + query + "&QueryType=" + queryType + "&MaxResults=100&start=" + start +
                "&SearchTarget=Book&output=js&Version=20131101";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        if (response == null || response.get("item") == null) {
            throw new NullPointerException("검색 결과가 없습니다.");
        }

        model.addAttribute("books", response != null ? response.get("item") : null);
        model.addAttribute("query", query);
        model.addAttribute("page", page);
        model.addAttribute("queryType", queryType);
        return "bookgo/bookList";
    }

    // 저자 검색 메소드
    @GetMapping("/search/author")
    public String searchByAuthor(@RequestParam String author, @RequestParam(defaultValue = "1") int page, Model model) {
        logger.info("저자 검색 요청: {}, 페이지: {}", author, page);

        String cleanAuthor = author.replaceAll("\\s*\\(지은이\\)", "");

        return searchBooks(cleanAuthor, page, "Author", model);
    }

    // 출판사 검색 메소드
    @GetMapping("/search/publisher")
    public String searchByPublisher(@RequestParam String publisher, @RequestParam(defaultValue = "1") int page, Model model) {
        logger.info("출판사 검색 요청: {}, 페이지: {}", publisher, page);
        return searchBooks(publisher, page, "Publisher", model);
    }
}
