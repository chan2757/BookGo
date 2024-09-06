package com.bookgo.service;

import com.bookgo.vo.BookDetailVO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
public class KyoboBookCrawlerService {

    // 크롤링을 수행하여 BookDetailVO에 데이터를 추가하는 메서드
    public BookDetailVO crawlBookDetails(BookDetailVO bookDetail) {
        // ChromeDriver 경로 설정 (chromedriver.exe가 위치한 경로로 수정)
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Joshua yoo\\chromedriver-win64\\chromedriver.exe");

        // ChromeOptions 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 헤드리스 모드 활성화
        options.addArguments("--disable-gpu"); // GPU 비활성화 (리눅스 환경에서 호환성 문제 방지)
        options.addArguments("--no-sandbox"); // 리눅스 환경에서 권한 문제 방지
        options.addArguments("--disable-dev-shm-usage"); // 리눅스 환경에서 메모리 문제 방지

        // WebDriver 설정
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // 대기 시간을 2초로 설정

        try {
            // 교보문고 검색 페이지로 이동 (ISBN을 포함한 URL로 이동)
            String searchUrl = "https://search.kyobobook.co.kr/search?keyword=" + bookDetail.getIsbn13();
            driver.get(searchUrl);

            // 검색 결과의 첫 번째 상품 링크 찾기 (대기)
            WebElement firstResultLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.prod_link")));
            firstResultLink.click(); // 첫 번째 링크 클릭으로 상세 페이지로 이동

            // 상세 페이지의 이미지 로딩 대기
            List<WebElement> imageElements = driver.findElements(By.cssSelector(".product_detail_area.detail_img .inner img"));

            // 이미지가 있을 경우에만 처리
            if (!imageElements.isEmpty()) {
                // 이미지 다운로드 및 설정 - ISBN으로 파일명을 설정
                bookDetail.setMainImg(downloadDetailImage(driver, bookDetail.getIsbn13()));
            } else {
                System.out.println("이미지가 존재하지 않습니다. 이미지 항목을 건너뜁니다.");
            }

            // 크롤링된 데이터를 BookDetailVO에 추가
            crawlBookIntroduction(driver, bookDetail);
            crawlBookCategory(driver, bookDetail);
            crawlAuthorInfo(driver, bookDetail);
            crawlContents(driver, bookDetail);
            crawlRecommendations(driver, bookDetail);
            crawlBookInside(driver, bookDetail);
            crawlPublisherReview(driver, bookDetail);

            System.out.println("크롤링 완료: " + bookDetail.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 드라이버 종료
            driver.quit();
        }

        return bookDetail;
    }
    // 책 소개 크롤링 메서드
    private void crawlBookIntroduction(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement bookIntroElement = driver.findElement(By.cssSelector(".intro_bottom .info_text"));
            String bookIntroduction = bookIntroElement.getText();
            bookDetail.setIntroduction(bookIntroduction);
        } catch (Exception e) {
            System.out.println("책 소개를 찾을 수 없습니다.");
        }
    }

    // 책 분야 정보 크롤링 메서드
    private void crawlBookCategory(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement bookCategoryElement = driver.findElement(By.cssSelector(".intro_category_list"));
            List<WebElement> categories = bookCategoryElement.findElements(By.tagName("a"));
            StringBuilder categoryBuilder = new StringBuilder("이 책이 속한 분야: ");
            for (WebElement category : categories) {
                categoryBuilder.append(category.getText()).append(" > ");
            }
            bookDetail.setCategory(categoryBuilder.toString());
        } catch (Exception e) {
            System.out.println("책 분야 정보를 찾을 수 없습니다.");
        }
    }

    // 저자 정보 크롤링 메서드
    private void crawlAuthorInfo(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement authorNameElement = driver.findElement(By.cssSelector(".round_gray_box .title_heading .person_link .text"));
            String authorName = authorNameElement.getText();
            WebElement authorInfoElement = driver.findElement(By.cssSelector(".round_gray_box .info_text"));
            String authorInfo = authorInfoElement.getText();
            bookDetail.setAuthorInfo(authorName + ": " + authorInfo);
        } catch (Exception e) {
            System.out.println("저자 정보를 찾을 수 없습니다.");
        }
    }



    private String downloadDetailImage(WebDriver driver, String isbn) {
        try {
            WebElement detailImage = driver.findElement(By.cssSelector(".product_detail_area.detail_img .inner img"));
            String imgUrl = detailImage.getAttribute("src");

            // 프로젝트의 static/mainimg 폴더 경로 설정
            String staticPath = System.getProperty("user.dir") + File.separator
                    + "src" + File.separator
                    + "main" + File.separator
                    + "resources" + File.separator
                    + "static" + File.separator
                    + "mainimg";

            // 디렉토리 생성
            File directory = new File(staticPath);
            if (!directory.exists()) {
                directory.mkdirs(); // 디렉토리가 없으면 생성
            }

            // ISBN을 파일 이름으로 사용, 파일 이름에 사용할 수 없는 문자는 대체
            String sanitizedIsbn = isbn.replaceAll("[\\\\/:*?\"<>|]", "_"); // 파일명에 사용할 수 없는 문자 대체
            String fileName = staticPath + File.separator + sanitizedIsbn + ".png";

            // 이미지 다운로드
            if (downloadImage(imgUrl, fileName)) {
                System.out.println("상세 페이지 이미지 다운로드 완료: " + new File(fileName).getAbsolutePath());
                // 브라우저에서 접근할 수 있도록 URL 형식으로 반환
                return "/mainimg/" + sanitizedIsbn + ".png";
            } else {
                System.out.println("상세 페이지 이미지를 다운로드할 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("다운로드할 이미지를 찾을 수 없습니다.");
            e.printStackTrace();
        }
        return null;
    }



    // 이미지 다운로드 메서드
    public boolean downloadImage(String urlStr, String fileName) {
        try (InputStream in = new URL(urlStr).openStream()) {
            BufferedImage image = ImageIO.read(in);
            if (image != null) {
                ImageIO.write(image, "png", new File(fileName));
                return true;
            } else {
                System.out.println("이미지를 읽을 수 없습니다: " + urlStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 목차 크롤링 메서드
    private void crawlContents(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement contentsElement = driver.findElement(By.cssSelector(".book_contents_list .book_contents_item"));
            String contents = contentsElement.getText();
            bookDetail.setContents(contents);
        } catch (Exception e) {
            System.out.println("목차를 찾을 수 없습니다.");
        }
    }

    // 추천사 크롤링 메서드
    private void crawlRecommendations(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement recommendList = driver.findElement(By.cssSelector(".recommend_list"));
            List<WebElement> recommendItems = recommendList.findElements(By.cssSelector(".recommend_item"));
            StringBuilder recommendations = new StringBuilder("추천사:");
            for (WebElement recommendItem : recommendItems) {
                String recommender = recommendItem.findElement(By.cssSelector(".title_heading")).getText();
                String recommendation = recommendItem.findElement(By.cssSelector(".info_text")).getText();
                recommendations.append(recommender).append(": ").append(recommendation).append("\n");
            }
            bookDetail.setRecommendations(recommendations.toString());
        } catch (Exception e) {
            System.out.println("추천사를 찾을 수 없습니다.");
        }
    }

    // 책 속으로 크롤링 메서드
    private void crawlBookInside(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement bookInsideElement = driver.findElement(By.cssSelector(".book_inside .info_text"));
            String bookInside = bookInsideElement.getText();
            bookDetail.setBookInside(bookInside);
        } catch (Exception e) {
            System.out.println("책 속으로를 찾을 수 없습니다.");
        }
    }

    // 출판사 서평 크롤링 메서드
    private void crawlPublisherReview(WebDriver driver, BookDetailVO bookDetail) {
        try {
            WebElement reviewElement = driver.findElement(By.cssSelector(".book_review .info_text"));
            String review = reviewElement.getText();
            bookDetail.setPublisherReview(review);
        } catch (Exception e) {
            System.out.println("출판사 서평을 찾을 수 없습니다.");
        }
    }
}
