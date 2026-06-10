package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CartPage.java
 * Page Object для страницы корзины.
 * URL: /cart.html
 */
public class CartPage extends BasePage {

    // ── ЛОКАТОРЫ ─────────────────────────────────────────────────────────────
    private static final By PAGE_TITLE        = By.cssSelector(".title");
    private static final By CART_ITEMS        = By.cssSelector(".cart_item");
    private static final By ITEM_NAMES        = By.cssSelector(".inventory_item_name");
    private static final By ITEM_PRICES       = By.cssSelector(".inventory_item_price");
    private static final By ITEM_QUANTITIES   = By.cssSelector(".cart_quantity");
    private static final By REMOVE_BTNS       = By.cssSelector("[data-test^='remove']");
    private static final By BTN_CONTINUE      = By.id("continue-shopping");
    private static final By BTN_CHECKOUT      = By.id("checkout");

    // ── МЕТОДЫ ───────────────────────────────────────────────────────────────

    /** Удалить первый товар из корзины */
    public CartPage removeFirstItem() {
        List<WebElement> btns = driver.findElements(REMOVE_BTNS);
        if (!btns.isEmpty()) btns.get(0).click();
        return this;
    }

    /** Удалить товар по индексу */
    public CartPage removeItemByIndex(int index) {
        List<WebElement> btns = driver.findElements(REMOVE_BTNS);
        if (index < btns.size()) btns.get(index).click();
        return this;
    }

    /** Перейти к оформлению заказа */
    public CheckoutPage proceedToCheckout() {
        click(BTN_CHECKOUT);
        return new CheckoutPage();
    }

    /** Вернуться к каталогу товаров */
    public InventoryPage continueShopping() {
        click(BTN_CONTINUE);
        return new InventoryPage();
    }

    // ── ПРОВЕРКИ ─────────────────────────────────────────────────────────────

    /** Получить заголовок страницы корзины */
    public String getTitle() {
        return getText(PAGE_TITLE);
    }

    /** Мы на странице корзины? */
    public boolean isOnCartPage() {
        return getCurrentUrl().contains("cart");
    }

    /** Количество товаров в корзине */
    public int getItemCount() {
        return driver.findElements(CART_ITEMS).size();
    }

    /** Пуста ли корзина? */
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /** Названия товаров в корзине */
    public List<String> getItemNames() {
        return driver.findElements(ITEM_NAMES)
                     .stream()
                     .map(WebElement::getText)
                     .collect(Collectors.toList());
    }

    /** Цены товаров в корзине */
    public List<String> getItemPrices() {
        return driver.findElements(ITEM_PRICES)
                     .stream()
                     .map(WebElement::getText)
                     .collect(Collectors.toList());
    }

    /** Содержит ли корзина товар с данным названием? */
    public boolean containsItem(String itemName) {
        return getItemNames().stream()
                             .anyMatch(name -> name.equalsIgnoreCase(itemName));
    }
}
