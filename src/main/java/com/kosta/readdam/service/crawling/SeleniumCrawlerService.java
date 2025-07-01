package com.kosta.readdam.service.crawling;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.kosta.readdam.entity.BookList;
import com.kosta.readdam.entity.enums.BookListCategory;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SeleniumCrawlerService {

    public List<BookList> fetchBookList(BookListCategory category) {
        // WebDriverManager로 ChromeDriver 자동 셋업
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--headless",
            "--disable-gpu",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--remote-allow-origins=*"
        );

        WebDriver driver = new ChromeDriver(options);
        try {
            // URL 결정 (Java 11 호환 if-else)
            String url;
            if (category == BookListCategory.WEEKLY) {
                url = "https://store.kyobobook.co.kr/bestseller/total/weekly";
            } else if (category == BookListCategory.MONTHLY) {
                url = "https://store.kyobobook.co.kr/bestseller/total/monthly";
            } else {
                url = "https://store.kyobobook.co.kr/bestseller/total/annual";
            }

            log.info("Selenium으로 페이지 열기: {}", url);
            driver.get(url);

            // **명시적 대기**: ul.list_goods가 로드될 때까지 최대 10초 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions
                .presenceOfElementLocated(By.cssSelector("ul.list_goods li")));

            List<WebElement> items = driver.findElements(By.cssSelector("ul.list_goods li"));
            log.info("Selenium 크롤링된 항목 개수 ({}): {}", category, items.size());

            return IntStream.range(0, Math.min(items.size(), 100))
                .mapToObj(i -> {
                    WebElement el = items.get(i);

                    // ISBN 추출
                    String href = el.findElement(By.cssSelector("a.link_detail"))
                                    .getAttribute("href");
                    String isbn = href.contains("barcode=")
                        ? href.split("barcode=")[1].split("&")[0]
                        : null;

                    // 이미지 파일명만
                    String imgUrl = el.findElement(By.cssSelector("a.cover img"))
                                      .getAttribute("src");
                    String imageName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);

                    // 작가·출판사
                    String author = el.findElement(By.cssSelector("div.info_inner span.author"))
                                      .getText();
                    String publisher = el.findElement(By.cssSelector("div.info_inner span.publisher"))
                                         .getText();

                    // ID 생성
                    String id = String.format("%s%03d", category.name(), i + 1);

                    return BookList.builder()
                        .id(id)
                        .isbn(isbn)
                        .imageName(imageName)
                        .author(author)
                        .publisher(publisher)
                        .category(category)
                        .build();
                })
                .collect(Collectors.toList());

        } finally {
            driver.quit();
        }
    }
}
