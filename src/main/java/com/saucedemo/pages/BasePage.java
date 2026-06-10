package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.saucedemo.config.Config.TIMEOUT_EXPLICIT;
import static com.saucedemo.utils.DriverManager.getDriver;

/**
 * BasePage.java
 * Базовый класс для всех Page Object'ов.
 * Содержит вспомогательные методы, которые используются на всех страницах:
 * ожидание элементов, клики, ввод текста, получение текста.
 *
 * Все страницы наследуют этот класс через: class LoginPage extends BasePage
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage() {
        this.driver = getDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_EXPLICIT));
    }

    // ── ОЖИДАНИЕ ─────────────────────────────────────────────────────────────

    /** Ждать пока элемент станет видимым */
    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Ждать пока элемент станет кликабельным */
    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Ждать пока текст появится на странице */
    protected boolean waitTextPresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /** Ждать пока URL содержит нужный фрагмент */
    protected boolean waitUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    // ── ДЕЙСТВИЯ ─────────────────────────────────────────────────────────────

    /** Кликнуть на элемент (с ожиданием кликабельности) */
    protected void click(By locator) {
        waitClickable(locator).click();
    }

    /** Очистить поле и ввести текст */
    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /** Получить текст элемента */
    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    /** Проверить, отображается ли элемент (без исключения если нет) */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Прокрутить страницу до элемента */
    protected void scrollTo(By locator) {
        WebElement el = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", el);
    }

    /** Получить текущий URL */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Получить заголовок страницы */
    public String getPageTitle() {
        return driver.getTitle();
    }
}
