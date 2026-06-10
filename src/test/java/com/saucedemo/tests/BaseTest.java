package com.saucedemo.tests;

import com.aventstack.extentreports.Status;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.DriverManager;
import com.saucedemo.utils.ReportManager;
import com.saucedemo.utils.ScreenshotUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest.java
 * Базовый класс для всех тестов.
 * Управляет:
 *   - запуском и остановкой браузера (@Before/@After)
 *   - инициализацией и сохранением отчётов
 *   - скриншотами при падении тестов
 *
 * Все тестовые классы наследуют этот класс:
 * class LoginTest extends BaseTest { ... }
 */
public abstract class BaseTest {

    protected LoginPage loginPage;

    // ── ЗАПУСК ОДИН РАЗ ПЕРЕД ВСЕМИ ТЕСТАМИ ──────────────────────────────────
    @BeforeSuite
    public void initSuite() {
        ReportManager.initReports();
        System.out.println("\n🚀 Запуск тестового набора SauceDemo");
    }

    // ── ЗАПУСК ПЕРЕД КАЖДЫМ ТЕСТОВЫМ МЕТОДОМ ─────────────────────────────────
    @BeforeMethod
    @Parameters({"browser", "headless"})
    public void setUp(
            @Optional("chrome")  String browser,
            @Optional("false")   String headless
    ) {
        // Запускаем браузер
        DriverManager.initDriver(browser, Boolean.parseBoolean(headless));

        // Создаём объект страницы входа
        loginPage = new LoginPage();
        loginPage.open();
    }

    // ── ПОСЛЕ КАЖДОГО ТЕСТОВОГО МЕТОДА ───────────────────────────────────────
    @AfterMethod
    public void tearDown(ITestResult result) {
        // Если тест упал — делаем скриншот и добавляем в отчёт
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = ScreenshotUtils.takeScreenshot(result.getName());

            if (screenshotPath != null && ReportManager.getTest() != null) {
                try {
                    ReportManager.getTest()
                        .addScreenCaptureFromPath(screenshotPath, "Скриншот при падении");
                    ReportManager.getTest()
                        .log(Status.FAIL, "❌ Тест упал: " + result.getThrowable().getMessage());
                } catch (Exception e) {
                    System.err.println("Не удалось прикрепить скриншот к отчёту");
                }
            }
        }

        // Закрываем браузер
        DriverManager.quitDriver();
    }

    // ── ЗАВЕРШЕНИЕ ВСЕХ ТЕСТОВ ────────────────────────────────────────────────
    @AfterSuite
    public void finishSuite() {
        ReportManager.flushReports();
        System.out.println("✅ Все тесты завершены. Отчёт сохранён.");
    }

    // ── ВСПОМОГАТЕЛЬНЫЙ МЕТОД ─────────────────────────────────────────────────

    /** Добавить шаг в HTML-отчёт */
    protected void logStep(String message) {
        System.out.println("  ▶ " + message);
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.INFO, message);
        }
    }

    /** Зафиксировать успешную проверку в отчёте */
    protected void logPass(String message) {
        System.out.println("  ✅ " + message);
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().log(Status.PASS, "✅ " + message);
        }
    }
}
