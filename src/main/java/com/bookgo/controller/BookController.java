package com.bookgo.controller;

import com.bookgo.vo.BookDpVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @GetMapping("/index")
    public String indexPage(Model model, @RequestParam(defaultValue = "Bestseller") String queryType) {
        logger.info("메인 페이지 요청, 쿼리 타입: {}", queryType);

        String apiUrl = BASE_URL + "ItemList.aspx?ttbkey=" + ALADIN_API_KEY +
                "&QueryType=" + queryType + "&MaxResults=10&start=1&SearchTarget=Book&output=js&Version=20131101";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        List<BookDpVO> books = parseBooks(response);
        model.addAttribute("books", books);
        model.addAttribute("queryType", queryType);

        return "bookgo/index"; // index.html로 매핑
    }

    @GetMapping("/index/books")
    public String loadBooks(@RequestParam String queryType, Model model) {
        logger.info("AJAX 도서 리스트 요청, 쿼리 타입: {}", queryType);

        String apiUrl = BASE_URL + "ItemList.aspx?ttbkey=" + ALADIN_API_KEY +
                "&QueryType=" + queryType + "&MaxResults=20&start=1&SearchTarget=Book&output=js&Version=20131101";

        RestTemplate restTemplate = new RestTemplate();

        try {
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            List<BookDpVO> books = parseBooks(response);

            model.addAttribute("books", books);
        } catch (Exception e) {
            logger.error("도서 데이터를 가져오는 중 오류가 발생했습니다.", e);
            model.addAttribute("books", List.of());
        }

        return "bookgo/index :: #book-list"; // index.html에서 #book-list 요소를 가져와서 렌더링
    }

    // JSON 데이터를 BookDpVO 리스트로 변환하는 메서드
    private List<BookDpVO> parseBooks(Map<String, Object> response) {
        List<BookDpVO> books = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON의 "item" 필드를 가져와서 List로 변환
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");

        if (items != null) {
            for (Map<String, Object> item : items) {
                try {
                    BookDpVO book = new BookDpVO();

                    // 필드 매핑
                    book.setTitle((String) item.get("title"));
                    book.setPublisher((String) item.get("publisher"));
                    book.setIsbn13((String) item.get("isbn13"));
                    book.setPriceStandard((int) item.get("priceStandard"));
                    book.setCover((String) item.get("cover"));
                    book.setCustomerReviewRank(item.get("customerReviewRank") instanceof Integer
                            ? (Integer) item.get("customerReviewRank")
                            : Double.parseDouble(item.get("customerReviewRank").toString()));

                    // 저자 문자열을 리스트로 변환
                    String author = (String) item.get("author");
                    if (author != null && !author.trim().isEmpty()) {
                        book.setAuthors(Arrays.asList(author.split(",\\s*")));
                    }

                    books.add(book);
                } catch (Exception e) {
                    logger.error("도서 데이터 파싱 중 오류 발생: {}", e.getMessage());
                }
            }
        }

        return books;
    }
}
