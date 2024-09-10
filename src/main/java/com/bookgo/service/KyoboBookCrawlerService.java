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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KyoboBookCrawlerService {

    // ExecutorService를 사용하여 비동기 작업을 병렬로 실행
    private final ExecutorService executor = Executors.newFixedThreadPool(4); // 4개의 스레드 풀을 사용

    public BookDetailVO crawlBookDetails(BookDetailVO bookDetail) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Joshua yoo\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-features=NetworkService");
        options.addArguments("--disable-javascript");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--disable-extensions");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            String searchUrl = "https://search.kyobobook.co.kr/search?keyword=" + bookDetail.getIsbn13();
            driver.get(searchUrl);

            WebElement firstResultLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.prod_link")));
            firstResultLink.click();

            List<WebElement> imageElements = driver.findElements(By.cssSelector(".product_detail_area.detail_img .inner img"));
            if (!imageElements.isEmpty()) {
                bookDetail.setMainImg(downloadDetailImage(driver, bookDetail.getIsbn13()));
            } else {
                System.out.println("이미지가 존재하지 않습니다. 이미지 항목을 건너뜁니다.");
            }

            // 비동기 크롤링 작업 병렬 실행
            CompletableFuture<Void> introFuture = CompletableFuture.runAsync(() -> crawlBookIntroduction(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("책 소개 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
            CompletableFuture<Void> categoryFuture = CompletableFuture.runAsync(() -> crawlBookCategory(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("책 분야 정보 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
            CompletableFuture<Void> authorFuture = CompletableFuture.runAsync(() -> crawlAuthorInfo(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("저자 정보 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
            CompletableFuture<Void> contentsFuture = CompletableFuture.runAsync(() -> crawlContents(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("목차 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
            CompletableFuture<Void> recommendationsFuture = CompletableFuture.runAsync(() -> crawlRecommendations(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("추천사 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
            CompletableFuture<Void> bookInsideFuture = CompletableFuture.runAsync(() -> crawlBookInside(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("책 속으로 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
            CompletableFuture<Void> publisherReviewFuture = CompletableFuture.runAsync(() -> crawlPublisherReview(driver, bookDetail), executor)
                    .exceptionally(ex -> {
                        System.out.println("출판사 서평 크롤링 중 오류 발생: " + ex.getMessage());
                        return null;
                    });

            // 모든 작업이 완료될 때까지 기다림
            CompletableFuture.allOf(introFuture, categoryFuture, authorFuture, contentsFuture, recommendationsFuture, bookInsideFuture, publisherReviewFuture).join();

            System.out.println("크롤링 완료: " + bookDetail.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            // ExecutorService는 모든 작업이 완료된 후에만 종료
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }

        return bookDetail;
    }

    private void crawlBookIntroduction(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".intro_bottom .info_text"));
            if (!elements.isEmpty()) {
                String bookIntroduction = elements.get(0).getAttribute("innerHTML");
                bookDetail.setIntroduction(bookIntroduction);
            } else {
                System.out.println("책 소개를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("책 소개 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private void crawlBookCategory(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".intro_category_list"));
            if (!elements.isEmpty()) {
                String bookCategory = elements.get(0).getText();
                bookDetail.setCategory(bookCategory);
            } else {
                System.out.println("책 분야 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("책 분야 정보 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private void crawlAuthorInfo(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> nameElements = driver.findElements(By.cssSelector(".round_gray_box .title_heading .person_link .text"));
            List<WebElement> infoElements = driver.findElements(By.cssSelector(".round_gray_box .info_text"));
            if (!nameElements.isEmpty() && !infoElements.isEmpty()) {
                String authorName = nameElements.get(0).getAttribute("innerHTML");
                String authorInfo = infoElements.get(0).getAttribute("innerHTML");
                bookDetail.setAuthorInfo(authorName + ": " + authorInfo);
            } else {
                System.out.println("저자 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("저자 정보 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private void crawlContents(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".book_contents_list .book_contents_item"));
            if (!elements.isEmpty()) {
                String contents = elements.get(0).getAttribute("innerHTML");
                bookDetail.setContents(contents);
            } else {
                System.out.println("목차를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("목차 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private void crawlRecommendations(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".recommend_list"));
            if (!elements.isEmpty()) {
                String recommendations = elements.get(0).getAttribute("innerHTML");
                bookDetail.setRecommendations(recommendations);
            } else {
                System.out.println("추천사를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("추천사 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private void crawlBookInside(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".book_inside .info_text"));
            if (!elements.isEmpty()) {
                String bookInside = elements.get(0).getAttribute("innerHTML");
                bookDetail.setBookInside(bookInside);
            } else {
                System.out.println("책 속으로를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("책 속으로 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private void crawlPublisherReview(WebDriver driver, BookDetailVO bookDetail) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".book_review .info_text"));
            if (!elements.isEmpty()) {
                String review = elements.get(0).getAttribute("innerHTML");
                bookDetail.setPublisherReview(review);
            } else {
                System.out.println("출판사 서평을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("출판사 서평 크롤링 중 오류 발생: " + e.getMessage());
        }
    }

    private String downloadDetailImage(WebDriver driver, String isbn) {
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(".product_detail_area.detail_img .inner img"));
            if (!elements.isEmpty()) {
                String imgUrl = elements.get(0).getAttribute("src");

                String staticPath = System.getProperty("user.dir") + File.separator
                        + "src" + File.separator
                        + "main" + File.separator
                        + "resources" + File.separator
                        + "static" + File.separator
                        + "mainimg";

                File directory = new File(staticPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String sanitizedIsbn = isbn.replaceAll("[\\\\/:*?\"<>|]", "_");
                String fileName = staticPath + File.separator + sanitizedIsbn + ".JPEG";

                if (downloadImage(imgUrl, fileName)) {
                    System.out.println("상세 페이지 이미지 다운로드 완료: " + new File(fileName).getAbsolutePath());
                    return "/mainimg/" + sanitizedIsbn + ".JPEG";
                } else {
                    System.out.println("상세 페이지 이미지를 다운로드할 수 없습니다.");
                }
            } else {
                System.out.println("다운로드할 이미지를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("이미지 다운로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean downloadImage(String urlStr, String fileName) {
        try (InputStream in = new URL(urlStr).openStream()) {
            BufferedImage image = ImageIO.read(in);
            if (image != null) {
                ImageIO.write(image, "JPEG", new File(fileName));
                return true;
            } else {
                System.out.println("이미지를 읽을 수 없습니다: " + urlStr);
            }
        } catch (Exception e) {
            System.out.println("이미지 다운로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
