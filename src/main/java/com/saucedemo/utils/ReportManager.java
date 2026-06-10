package com.saucedemo.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

import static com.saucedemo.config.Config.REPORT_FILE;
import static com.saucedemo.config.Config.REPORTS_DIR;

/**
 * ReportManager.java
 * Управляет созданием HTML-отчёта через ExtentReports.
 *
 * Как работает:
 *   1. initReports() — вызывается один раз в начале всех тестов
 *   2. createTest()  — вызывается для каждого тест-метода
 *   3. getTest()     — возвращает текущий тест для добавления шагов
 *   4. flushReports() — сохраняет отчёт на диск (в конце всех тестов)
 */
public class ReportManager {

    private static ExtentReports extent;

    // ThreadLocal — у каждого потока/теста свой ExtentTest
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    /**
     * Инициализировать отчёт. Вызывать один раз перед всеми тестами.
     */
    public static void initReports() {
        // Создаём папку test-output если нет
        new File(REPORTS_DIR).mkdirs();

        // Настраиваем внешний вид отчёта
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_FILE);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("SauceDemo — Отчёт о тестировании");
        sparkReporter.config().setReportName("Автотесты: авторизация, корзина, оформление заказа");
        sparkReporter.config().setTimeStampFormat("dd.MM.yyyy HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Системная информация в отчёте
        extent.setSystemInfo("Сайт",       "https://www.saucedemo.com");
        extent.setSystemInfo("Тестировщик", "Ревунов Д.В.");
        extent.setSystemInfo("Стек",        "Java + Selenium + TestNG + ExtentReports");
        extent.setSystemInfo("ОС",          System.getProperty("os.name"));
        extent.setSystemInfo("Java",        System.getProperty("java.version"));
    }

    /**
     * Создать новый тест в отчёте.
     * Вызывать в @BeforeMethod каждого теста.
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    /**
     * Получить текущий тест для добавления шагов.
     */
    public static ExtentTest getTest() {
        return test.get();
    }

    /**
     * Сохранить отчёт на диск.
     * Вызывать один раз после всех тестов.
     */
    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            System.out.println("\n📊 Отчёт сохранён: " + REPORT_FILE);
        }
    }
}
