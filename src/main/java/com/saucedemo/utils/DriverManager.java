package com.saucedemo.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

import static com.saucedemo.config.Config.*;

/**
 * DriverManager.java
 * Управляет жизненным циклом браузера: создаёт и закрывает WebDriver.
 *
 * ThreadLocal<WebDriver> нужен для параллельного запуска тестов —
 * каждый поток получает свой экземпляр браузера.
 */
public class DriverManager {

    // ThreadLocal — у каждого потока свой WebDriver
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Создать браузер и сохранить в ThreadLocal.
     *
     * @param browser  "chrome" или "firefox"
     * @param headless true = без окна (быстрее), false = с окном (видно что происходит)
     */
    public static void initDriver(String browser, boolean headless) {
        WebDriver webDriver;

        if ("firefox".equalsIgnoreCase(browser)) {
            // Firefox
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            if (headless) options.addArguments("--headless");
            webDriver = new FirefoxDriver(options);

        } else {
            // Chrome (по умолчанию)
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless=new"); // новый headless Chrome
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
            }
            options.addArguments("--window-size=1366,768");
            options.addArguments("--disable-notifications");
            webDriver = new ChromeDriver(options);
        }

        // Неявное ожидание — Selenium будет ждать появления элемента
        webDriver.manage().timeouts()
                 .implicitlyWait(Duration.ofSeconds(TIMEOUT_IMPLICIT));

        // Максимальное время загрузки страницы
        webDriver.manage().timeouts()
                 .pageLoadTimeout(Duration.ofSeconds(TIMEOUT_PAGE));

        // Разворачиваем окно браузера
        webDriver.manage().window().maximize();

        driver.set(webDriver);
    }

    /**
     * Получить текущий экземпляр WebDriver.
     * Вызывается из тестов и Page Object'ов.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Закрыть браузер и удалить из ThreadLocal.
     * Вызывается после каждого теста или набора тестов.
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
