package com.saucedemo.pages;

import org.openqa.selenium.By;

/**
 * CheckoutPage.java
 * Page Object для страниц оформления заказа.
 * Шаг 1: /checkout-step-one.html  — ввод данных покупателя
 * Шаг 2: /checkout-step-two.html  — обзор заказа
 * Завершение: /checkout-complete.html — подтверждение
 */
public class CheckoutPage extends BasePage {

    // ── ЛОКАТОРЫ — ШАГ 1 (данные покупателя) ─────────────────────────────────
    private static final By INPUT_FIRST_NAME = By.id("first-name");
    private static final By INPUT_LAST_NAME  = By.id("last-name");
    private static final By INPUT_ZIP        = By.id("postal-code");
    private static final By BTN_CONTINUE     = By.id("continue");
    private static final By BTN_CANCEL       = By.id("cancel");
    private static final By ERROR_MESSAGE    = By.cssSelector("[data-test='error']");

    // ── ЛОКАТОРЫ — ШАГ 2 (обзор заказа) ──────────────────────────────────────
    private static final By SUMMARY_ITEMS    = By.cssSelector(".cart_item");
    private static final By SUMMARY_SUBTOTAL = By.cssSelector(".summary_subtotal_label");
    private static final By SUMMARY_TAX      = By.cssSelector(".summary_tax_label");
    private static final By SUMMARY_TOTAL    = By.cssSelector(".summary_total_label");
    private static final By BTN_FINISH       = By.id("finish");
    private static final By BTN_CANCEL_2     = By.id("cancel");

    // ── ЛОКАТОРЫ — ЗАВЕРШЕНИЕ ──────────────────────────────────────────────────
    private static final By COMPLETE_HEADER  = By.cssSelector(".complete-header");
    private static final By COMPLETE_TEXT    = By.cssSelector(".complete-text");
    private static final By BTN_BACK_HOME    = By.id("back-to-products");

    // ── МЕТОДЫ — ШАГ 1 ───────────────────────────────────────────────────────

    /** Ввести имя покупателя */
    public CheckoutPage enterFirstName(String firstName) {
        type(INPUT_FIRST_NAME, firstName);
        return this;
    }

    /** Ввести фамилию покупателя */
    public CheckoutPage enterLastName(String lastName) {
        type(INPUT_LAST_NAME, lastName);
        return this;
    }

    /** Ввести почтовый индекс */
    public CheckoutPage enterZip(String zip) {
        type(INPUT_ZIP, zip);
        return this;
    }

    /** Заполнить все поля и нажать «Continue» */
    public CheckoutPage fillShippingInfo(String firstName, String lastName, String zip) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterZip(zip);
        clickContinue();
        return this;
    }

    /** Нажать «Continue» для перехода к шагу 2 */
    public CheckoutPage clickContinue() {
        click(BTN_CONTINUE);
        return this;
    }

    /** Нажать «Cancel» для отмены оформления */
    public CartPage clickCancel() {
        click(BTN_CANCEL);
        return new CartPage();
    }

    /** Получить текст ошибки валидации */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /** Отображается ли ошибка валидации? */
    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    // ── МЕТОДЫ — ШАГ 2 ───────────────────────────────────────────────────────

    /** Получить итоговую сумму (подытог без налога) */
    public String getSubtotal() {
        return getText(SUMMARY_SUBTOTAL);
    }

    /** Получить сумму налога */
    public String getTax() {
        return getText(SUMMARY_TAX);
    }

    /** Получить итоговую сумму с налогом */
    public String getTotal() {
        return getText(SUMMARY_TOTAL);
    }

    /** Получить количество товаров в обзоре заказа */
    public int getSummaryItemCount() {
        return driver.findElements(SUMMARY_ITEMS).size();
    }

    /** Нажать «Finish» для завершения заказа */
    public CheckoutPage clickFinish() {
        click(BTN_FINISH);
        return this;
    }

    // ── МЕТОДЫ — ЗАВЕРШЕНИЕ ──────────────────────────────────────────────────

    /** Получить заголовок страницы подтверждения */
    public String getCompleteHeader() {
        return getText(COMPLETE_HEADER);
    }

    /** Получить текст подтверждения заказа */
    public String getCompleteText() {
        return getText(COMPLETE_TEXT);
    }

    /** Вернуться на страницу каталога после завершения заказа */
    public InventoryPage backToProducts() {
        click(BTN_BACK_HOME);
        return new InventoryPage();
    }

    // ── ПРОВЕРКИ ─────────────────────────────────────────────────────────────

    /** Мы на странице ввода данных (шаг 1)? */
    public boolean isOnStep1() {
        return getCurrentUrl().contains("checkout-step-one");
    }

    /** Мы на странице обзора (шаг 2)? */
    public boolean isOnStep2() {
        return getCurrentUrl().contains("checkout-step-two");
    }

    /** Мы на странице подтверждения? */
    public boolean isOnCompletePage() {
        return getCurrentUrl().contains("checkout-complete");
    }
}
