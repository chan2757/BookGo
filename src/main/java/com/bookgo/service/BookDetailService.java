package com.bookgo.service;

import com.bookgo.controller.BookController;
import com.bookgo.vo.BookDetailVO;
import com.bookgo.vo.BookDpVO;
import com.bookgo.mapper.BookDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BookDetailService {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private static final String ALADIN_API_KEY = "ttbyyd0000939001"; // 알라딘 API 키
    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/";

    private final BookDetailMapper bookDetailMapper;
    private final KyoboBookCrawlerService kyoboBookCrawlerService;
    private final CategoryService categoryService;
    ;


    // 생성자
    public BookDetailService(BookDetailMapper bookDetailMapper, KyoboBookCrawlerService kyoboBookCrawlerService, CategoryService categoryService) {
        this.bookDetailMapper = bookDetailMapper;
        this.kyoboBookCrawlerService = kyoboBookCrawlerService;
        this.categoryService = categoryService;
    }

    // @Transactional 애노테이션은 메서드에 적용
    @Transactional
    public BookDetailVO getBookDetail(BookDpVO dp) {
        // DP에서 ISBN13을 추출하여 사용
        String isbn13 = dp.getIsbn13();

        // MyBatis를 통해 데이터베이스에서 책 정보를 조회
        BookDetailVO bookDetail = bookDetailMapper.selectBookDetailByIsbn13(isbn13);

        System.out.println(dp.toString());

        if (bookDetail != null) {
            System.out.println("db에 정보가 있습니다.");
        }

        // 데이터베이스에 정보가 없으면 DP와 크롤링 정보를 사용하여 데이터를 수집 및 저장
        if (bookDetail == null) {
            // DP 정보로 임시 BookDetailVO를 생성
            System.out.println("db에 정보가 없습니다. 크롤링을 통해 정보를 수집합니다.");
            bookDetail = createDetailFromDp(dp);

            // 크롤링을 통한 상세 정보 추가
            bookDetail = kyoboBookCrawlerService.crawlBookDetails(bookDetail);

            String categoryNum = categoryService.getCategoryNumber(bookDetail.getCategory());


            bookDetail.setCategoryNumber(categoryNum);


            try {
                bookDetailMapper.insertBookDetail(bookDetail); // MyBatis를 통해 데이터베이스에 저장
                System.out.println("BookDetailService: 데이터베이스에 저장 완료");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("BookDetailService: 데이터베이스 저장 중 오류 발생");
            }
            System.out.println(bookDetail);
        }

        return bookDetail;
    }

    // DP 정보를 사용하여 BookDetailVO를 생성하는 메서드
    private BookDetailVO createDetailFromDp(BookDpVO dp) {
        BookDetailVO bookDetail = new BookDetailVO();
        bookDetail.setIsbn13(dp.getIsbn13());
        bookDetail.setTitle(dp.getTitle());

        // DP의 저자 리스트를 그대로 설정 - 이 부분에서 authors가 제대로 설정되었는지 확인 필요
        if (dp.getAuthors() != null) {
            bookDetail.setAuthors(dp.getAuthors());
            System.out.println("Authors from DP: " + dp.getAuthors()); // 디버그용 로그 추가
        } else {
            System.out.println("Authors are null in DP");
        }

        bookDetail.setPublisher(dp.getPublisher());
        bookDetail.setPriceStandard(dp.getPriceStandard());
        bookDetail.setCustomerReviewRank(dp.getCustomerReviewRank());
        bookDetail.setCover(dp.getCover());
        return bookDetail;
    }

    // 알라딘 API에서 책 정보를 가져와 BookDpVO를 생성하는 메서드
    public BookDpVO createBookDpFromApi(String isbn13) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = BASE_URL + "ItemLookUp.aspx?ttbkey=" + ALADIN_API_KEY +
                "&itemIdType=ISBN13&ItemId=" + isbn13 + "&output=js&Version=20131101";

        // 알라딘 API로부터 데이터를 가져옴
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        // 응답이 없거나 잘못된 경우 null 반환
        if (response == null || !response.containsKey("item")) {
            System.out.println("알라딘 API 응답이 없습니다.");
            return null;
        }

        // 응답에서 필요한 데이터를 추출하고 BookDpVO에 설정
        Map<String, Object> item = ((List<Map<String, Object>>) response.get("item")).get(0);
        BookDpVO bookDpVO = new BookDpVO();
        bookDpVO.setIsbn13(isbn13);
        bookDpVO.setTitle((String) item.get("title"));

        // API에서 받은 저자 데이터를 리스트로 변환하여 설정
        String authorString = (String) item.get("author");
        if (authorString != null) {
            bookDpVO.setAuthor(authorString);
        } else {
            System.out.println("저자 정보가 없습니다.");
        }

        bookDpVO.setPublisher((String) item.get("publisher"));
        bookDpVO.setPriceStandard(Integer.parseInt(item.get("priceStandard").toString()));
        bookDpVO.setCustomerReviewRank(Double.parseDouble(item.get("customerReviewRank").toString()));
        bookDpVO.setCover((String) item.get("cover"));

        System.out.println("알라딘 API로부터 생성된 BookDpVO: " + bookDpVO);

        return bookDpVO;
    }

    public List<BookDpVO> makeDpList(String queryType) {
        List<BookDpVO> bookList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = BASE_URL + "ItemList.aspx?ttbkey=" + ALADIN_API_KEY +
                "&QueryType=" + queryType + "&MaxResults=30&start=1&SearchTarget=Book&output=js&Version=20131101";

        // API 호출하여 데이터를 가져옴
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        // 응답이 없거나 잘못된 경우 빈 리스트 반환
        if (response == null || !response.containsKey("item")) {
            return bookList;
        }

        // 응답에서 item 리스트를 가져옴
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");

        // 각 item을 BookDpVO 객체로 변환하여 리스트에 추가
        for (Map<String, Object> item : items) {
            BookDpVO book = createBookDpFromApi(item);
            bookList.add(book);
        }

        // 리스트의 내용을 로그로 출력
        logger.debug("Book list: {}", bookList);

        return bookList;
    }

    // API 응답을 BookDpVO로 변환하는 메소드
    private BookDpVO createBookDpFromApi(Map<String, Object> item) {
        BookDpVO bookDpVO = new BookDpVO();

        bookDpVO.setIsbn13((String) item.get("isbn13"));
        bookDpVO.setTitle((String) item.get("title"));
        bookDpVO.setPublisher((String) item.get("publisher"));
        bookDpVO.setCover((String) item.get("cover"));

        // 저자를 문자열에서 리스트로 변환
        String authorString = (String) item.get("author");
        if (authorString != null && !authorString.trim().isEmpty()) {
            bookDpVO.setAuthors(Arrays.asList(authorString.split(",\\s*")));
        }

        // 가격과 평점이 있는 경우 설정
        if (item.get("priceStandard") != null) {
            bookDpVO.setPriceStandard(Integer.parseInt(item.get("priceStandard").toString()));
        }

        if (item.get("customerReviewRank") != null) {
            bookDpVO.setCustomerReviewRank(Double.parseDouble(item.get("customerReviewRank").toString()));
        }

        return bookDpVO;
    }


    public List<BookDpVO> getBookDetailByCategoryId(String categoryId) {
        return bookDetailMapper.selectBookDpByCategoryId(categoryId);
    }
}


