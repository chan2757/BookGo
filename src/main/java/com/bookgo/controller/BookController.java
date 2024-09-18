package com.bookgo.controller;

import com.bookgo.service.BookDetailService;
import com.bookgo.vo.BookDpVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/bookgo/books", "/bookgo/book"})
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private static final String ALADIN_API_KEY = "ttbyyd0000939001";
    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/";

    private final BookDetailService bookDetailService;

    public BookController(BookDetailService bookDetailService) {
        this.bookDetailService = bookDetailService;
    }

    @GetMapping("/list")
    public String getBookList(@RequestParam(defaultValue = "Bestseller") String queryType,
                              @RequestParam(defaultValue = "1") int page, Model model) {
        logger.info("도서 리스트 요청: {} 페이지: {}", queryType, page);

        int maxResults = 40;  // 한 페이지에 보여줄 최대 결과 수
        int startIndex = (page - 1) * maxResults + 1;

        String apiUrl = BASE_URL + "ItemList.aspx?ttbkey=" + ALADIN_API_KEY +
                "&QueryType=" + queryType + "&MaxResults=" + maxResults +
                "&start=" + startIndex + "&SearchTarget=Book&output=js&Version=20131101";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        if (response == null || response.get("item") == null) {
            throw new NullPointerException("도서 목록이 없습니다.");
        }

        // item 목록을 List<Map<String, Object>>로 변환
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");

        // 총 결과 수를 받아와 총 페이지 수 계산
        int totalResults = Math.min((int) response.get("totalResults"), 200); // 총 검색 결과 수
        int totalPages = (int) Math.ceil((double) totalResults / maxResults); // 총 페이지 수 계산

        // items 리스트를 BookDpVO 리스트로 변환
        List<BookDpVO> books = items.stream().map(item -> {
            BookDpVO book = new BookDpVO();
            book.setTitle((String) item.get("title"));
            book.setPublisher((String) item.get("publisher"));
            book.setIsbn13((String) item.get("isbn13"));
            book.setPriceStandard((int) item.get("priceStandard"));
            book.setCustomerReviewRank(Double.parseDouble(item.get("customerReviewRank").toString()));
            book.setCover((String) item.get("cover"));
            String authors = (String) item.get("author");
            if (authors != null && !authors.isEmpty()) {
                // 저자 문자열을 쉼표로 분리한 후, 각 항목의 공백을 제거
                List<String> authorList = Arrays.stream(authors.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                book.setAuthors(authorList);  // setAuthors를 사용하여 리스트로 저장
            } else {
                book.setAuthors(Collections.emptyList());  // 저자가 없을 경우 빈 리스트 설정
            }
            return book;
        }).collect(Collectors.toList());



        model.addAttribute("from", "list");
        model.addAttribute("books", books);
        model.addAttribute("queryType", queryType);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages); // 실제 계산한 총 페이지 수 추가

        return "bookgo/bookList";
    }


    @GetMapping("/search")
    public String searchBooks(@RequestParam String query,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "Keyword") String queryType, Model model) {
        logger.info("도서 검색 요청: {}, 페이지: {}", query, page);

        int maxResults = 40;
        int start = (page - 1) * maxResults + 1;

        String apiUrl = BASE_URL + "ItemSearch.aspx?ttbkey=" + ALADIN_API_KEY +
                "&Query=" + query + "&QueryType=" + queryType + "&MaxResults=" + maxResults + "&start=" + page +
                "&SearchTarget=Book&output=js&Version=20131101";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        if (response == null || response.get("item") == null) {
            throw new NullPointerException("검색 결과가 없습니다.");
        }

        // item 목록을 List<Map<String, Object>>로 변환
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");

        // 총 검색 결과 수를 받아와 전체 페이지 수 계산
        int totalResults = Math.min((int) response.get("totalResults"), 200); // 총 검색 결과 수
        int totalPages = (int) Math.ceil((double) totalResults / maxResults); // 전체 페이지 수 계산

        // items 리스트를 BookDpVO 리스트로 변환
        List<BookDpVO> books = items.stream().map(item -> {
            BookDpVO book = new BookDpVO();
            book.setTitle((String) item.get("title"));
            book.setPublisher((String) item.get("publisher"));
            book.setIsbn13((String) item.get("isbn13"));
            book.setPriceStandard((int) item.get("priceStandard"));
            book.setCustomerReviewRank(Double.parseDouble(item.get("customerReviewRank").toString()));
            book.setCover((String) item.get("cover"));
            String authors = (String) item.get("author");
            if (authors != null && !authors.isEmpty()) {
                // 저자 문자열을 쉼표로 분리한 후, 각 항목의 공백을 제거
                List<String> authorList = Arrays.stream(authors.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                book.setAuthors(authorList);  // setAuthors를 사용하여 리스트로 저장
            } else {
                book.setAuthors(Collections.emptyList());  // 저자가 없을 경우 빈 리스트 설정
            }
            return book;
        }).collect(Collectors.toList());

        // books 리스트를 모델에 추가
        model.addAttribute("from", "search");
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        model.addAttribute("queryType", queryType);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);  // 실제 계산한 총 페이지 수 추가

        System.out.println("페이지"+page);

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
