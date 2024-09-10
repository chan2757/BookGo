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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KyoboBookCrawlerService {

    // ExecutorService를 사용하여 비동기 작업을 병렬로 실행
    private final ExecutorService executor = Executors.newFixedThreadPool(8); // 8개의 스레드 풀을 사용

    public BookDetailVO crawlBookDetails(BookDetailVO bookDetail) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Joshua yoo\\chromedriver-win64\\chromedriver.exe");

        // 1단계: 검색 단계에서 이미지 및 기타 리소스를 로드하지 않도록 설정
        ChromeOptions searchOptions = new ChromeOptions();
        searchOptions.addArguments("--headless");
        searchOptions.addArguments("--disable-gpu");
        searchOptions.addArguments("--no-sandbox");
        searchOptions.addArguments("--disable-dev-shm-usage");
        searchOptions.addArguments("--disable-features=NetworkService");
        searchOptions.addArguments("--disable-javascript");
        searchOptions.addArguments("--disable-extensions");
        // 불필요한 리소스 차단
        searchOptions.setExperimentalOption("prefs", Map.of(
                "profile.managed_default_content_settings.images", 2,  // 이미지 로딩 비활성화
                "profile.managed_default_content_settings.stylesheets", 2,  // CSS 로딩 비활성화
                "profile.managed_default_content_settings.fonts", 2,       // 폰트 로딩 비활성화
                "profile.managed_default_content_settings.plugins", 2,     // 플러그인 비활성화
                "profile.managed_default_content_settings.popups", 2       // 팝업 비활성화
        ));

        WebDriver searchDriver = new ChromeDriver(searchOptions);
        WebDriverWait searchWait = new WebDriverWait(searchDriver, Duration.ofSeconds(5));

        try {
            long startTime = System.currentTimeMillis();
            String searchUrl = "https://search.kyobobook.co.kr/search?keyword=" + bookDetail.getIsbn13();
            searchDriver.get(searchUrl);
            logTime("Search and page load", startTime);

            // 첫 번째 검색 결과의 URL을 직접 가져옴
            WebElement firstResultLink = searchWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".prod_thumb_box.size_lg a.prod_link")));
            String firstResultUrl = firstResultLink.getAttribute("href"); // 첫 번째 결과의 URL 추출
            logTime("Extract first search result URL", System.currentTimeMillis());

            // 2단계: 첫 번째 검색 결과 페이지 로드 (이미지 및 필요한 리소스 로드 활성화)
            ChromeOptions detailOptions = new ChromeOptions();
            detailOptions.addArguments("--headless");
            detailOptions.addArguments("--disable-gpu");
            detailOptions.addArguments("--no-sandbox");
            detailOptions.addArguments("--disable-dev-shm-usage");
            detailOptions.addArguments("--disable-features=NetworkService");
            detailOptions.addArguments("--disable-javascript");
            detailOptions.addArguments("--disable-extensions");

            WebDriver detailDriver = new ChromeDriver(detailOptions);
            WebDriverWait detailWait = new WebDriverWait(detailDriver, Duration.ofSeconds(1));
            detailDriver.get(firstResultUrl); // 추출한 URL로 이동

            // 이미지 다운로드 작업을 비동기적으로 처리
            CompletableFuture<Void> imageDownloadFuture = CompletableFuture.runAsync(() -> {
                long imageDownloadStartTime = System.currentTimeMillis();
                List<WebElement> imageElements = detailDriver.findElements(By.cssSelector(".product_detail_area.detail_img .inner img"));
                if (!imageElements.isEmpty()) {
                    bookDetail.setMainImg(downloadDetailImage(detailDriver, bookDetail.getIsbn13()));
                } else {
                    System.out.println("이미지가 존재하지 않습니다. 이미지 항목을 건너뜁니다.");
                }
                logTime("Download main image", imageDownloadStartTime);
            }, executor);

            // 비동기 크롤링 작업 병렬 실행
            CompletableFuture<Void> introFuture = measureExecutionTime("Book Introduction", () -> crawlBookIntroduction(detailDriver, bookDetail));
            CompletableFuture<Void> categoryFuture = measureExecutionTime("Book Category", () -> crawlBookCategory(detailDriver, bookDetail));
            CompletableFuture<Void> authorFuture = measureExecutionTime("Author Info", () -> crawlAuthorInfo(detailDriver, bookDetail));
            CompletableFuture<Void> contentsFuture = measureExecutionTime("Contents", () -> crawlContents(detailDriver, bookDetail));
            CompletableFuture<Void> recommendationsFuture = measureExecutionTime("Recommendations", () -> crawlRecommendations(detailDriver, bookDetail));
            CompletableFuture<Void> bookInsideFuture = measureExecutionTime("Book Inside", () -> crawlBookInside(detailDriver, bookDetail));
            CompletableFuture<Void> publisherReviewFuture = measureExecutionTime("Publisher Review", () -> crawlPublisherReview(detailDriver, bookDetail));

            // 모든 작업이 완료될 때까지 기다림
            CompletableFuture.allOf(imageDownloadFuture, introFuture, categoryFuture, authorFuture, contentsFuture, recommendationsFuture, bookInsideFuture, publisherReviewFuture)
                    .whenComplete((result, error) -> {
                        if (!executor.isShutdown()) {
                            executor.shutdown();
                        }
                    }).join();

            System.out.println("크롤링 완료: " + bookDetail.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            searchDriver.quit();
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }

        return bookDetail;
    }

    private CompletableFuture<Void> measureExecutionTime(String taskName, Runnable task) {
        return CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            task.run();
            logTime(taskName, start);
        }, executor).exceptionally(ex -> {
            System.out.println(taskName + " 크롤링 중 오류 발생: " + ex.getMessage());
            return null;
        });
    }

    private void logTime(String taskName, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(taskName + " 소요 시간: " + elapsed + "ms");
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
            long startTime = System.currentTimeMillis();
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

                if (downloadAndOptimizeImage(imgUrl, fileName)) {
                    logTime("Image download", startTime);
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

    // 이미지를 다운로드하고 최적화하는 메서드
    public boolean downloadAndOptimizeImage(String urlStr, String fileName) {
        try (InputStream in = new URL(urlStr).openStream()) {
            BufferedImage originalImage = ImageIO.read(in);
            if (originalImage != null) {
                // 이미지 압축: 품질을 낮춰서 파일 크기를 줄이는 방법
                BufferedImage optimizedImage = optimizeImage(originalImage);

                // JPEG 형식으로 저장
                ImageIO.write(optimizedImage, "JPEG", new File(fileName));

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

    // 이미지 최적화 메서드
    private BufferedImage optimizeImage(BufferedImage originalImage) {
        // 최적화된 이미지 크기 설정 (예: 80% 축소)
        int width = (int) (originalImage.getWidth() * 0.8);
        int height = (int) (originalImage.getHeight() * 0.8);

        BufferedImage optimizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = optimizedImage.createGraphics();

        // 이미지 품질 최적화를 위한 설정
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();

        return optimizedImage;
    }
}
