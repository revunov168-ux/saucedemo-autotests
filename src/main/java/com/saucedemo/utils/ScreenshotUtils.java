package com.saucedemo.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.saucedemo.config.Config.SCREENSHOTS_DIR;

/**
 * ScreenshotUtils.java
 * Делает скриншот текущего состояния браузера.
 * Вызывается автоматически при падении теста.
 */
public class ScreenshotUtils {

    /**
     * Сделать скриншот и сохранить в папку screenshots/.
     *
     * @param testName имя теста — будет частью имени файла
     * @return путь к сохранённому файлу (для вставки в отчёт)
     */
    public static String takeScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) return null;

        try {
            // Создаём папку если нет
            Path dir = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            // Имя файла: testName_2026-06-08_14-30-00.png
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName + "_" + timestamp + ".png";
            Path filePath = dir.resolve(fileName);

            // Делаем скриншот
            File screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), filePath);

            System.out.println("📸 Скриншот сохранён: " + filePath);
            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            System.err.println("⚠ Не удалось сохранить скриншот: " + e.getMessage());
            return null;
        }
    }
}
