package com.saucedemo.pages;

import org.openqa.selenium.By;

import static com.saucedemo.config.Config.BASE_URL;

/**
 * LoginPage.java
 * Page Object для страницы входа saucedemo.com.
 *
 * Принцип Page Object Model (POM):
 *   - Локаторы (By.id, By.cssSelector...) хранятся здесь, не в тестах
 *   - Тесты вызывают методы: loginPage.login("user", "pass")
 *   - Если верстка изменится — меняем только этот файл
 */
public class LoginPage extends BasePage {

    // ── ЛОКАТОРЫ ─────────────────────────────────────────────────────────────
    // Как найти: F12 → Ctrl+Shift+C → кликнуть на элемент → скопировать id/class

    private static final By INPUT_USERNAME  = By.id("user-name");
    private static final By INPUT_PASSWORD  = By.id("password");
    private static final By BTN_LOGIN       = By.id("login-button");
    private static final By ERROR_MESSAGE   = By.cssSelector("[data-test='error']");
    private static final By ERROR_ICON      = By.cssSelector(".error-button");

    // ── МЕТОДЫ ───────────────────────────────────────────────────────────────

    /** Открыть страницу входа */
    public LoginPage open() {
        driver.get(BASE_URL);
        return this;
    }

    /** Ввести логин */
    public LoginPage enterUsername(String username) {
        type(INPUT_USERNAME, username);
        return this;
    }

    /** Ввести пароль */
    public LoginPage enterPassword(String password) {
        type(INPUT_PASSWORD, password);
        return this;
    }

    /** Нажать кнопку «Login» */
    public LoginPage clickLogin() {
        click(BTN_LOGIN);
        return this;
    }

    /**
     * Выполнить полный вход.
     * Возвращает InventoryPage если вход успешен.
     */
    public InventoryPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new InventoryPage();
    }

    /**
     * Попытаться войти (без гарантии успеха).
     * Используется для негативных тестов — когда ожидается ошибка.
     */
    public LoginPage attemptLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    // ── ПРОВЕРКИ ─────────────────────────────────────────────────────────────

    /** Получить текст ошибки авторизации */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /** Отображается ли ошибка авторизации? */
    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    /** Мы на странице входа? */
    public boolean isOnLoginPage() {
        return isDisplayed(INPUT_USERNAME) && isDisplayed(BTN_LOGIN);
    }
}
