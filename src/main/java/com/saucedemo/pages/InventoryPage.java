package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * InventoryPage.java
 * Page Object для страницы каталога товаров (после входа).
 * URL: /inventory.html
 */
public class InventoryPage extends BasePage {

    // ── ЛОКАТОРЫ ─────────────────────────────────────────────────────────────
    private static final By PAGE_TITLE         = By.cssSelector(".title");
    private static final By INVENTORY_LIST     = By.cssSelector(".inventory_list");
    private static final By PRODUCT_NAMES      = By.cssSelector(".inventory_item_name");
    private static final By PRODUCT_PRICES     = By.cssSelector(".inventory_item_price");
    private static final By ADD_TO_CART_BTNS   = By.cssSelector("[data-test^='add-to-cart']");
    private static final By REMOVE_BTNS        = By.cssSelector("[data-test^='remove']");
    private static final By CART_BADGE         = By.cssSelector(".shopping_cart_badge");
    private static final By CART_ICON          = By.cssSelector(".shopping_cart_link");
    private static final By SORT_DROPDOWN      = By.cssSelector("[data-test='product-sort-container']");
    private static final By BURGER_MENU        = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK        = By.id("logout_sidebar_link");

    // Кнопка «Add to cart» для конкретного товара по его data-test атрибуту
    private By addToCartBtn(String productId) {
        return By.cssSelector("[data-test='add-to-cart-" + productId + "']");
    }

    // ── МЕТОДЫ ───────────────────────────────────────────────────────────────

    /** Добавить первый попавшийся товар в корзину */
    public InventoryPage addFirstItemToCart() {
        List<WebElement> btns = driver.findElements(ADD_TO_CART_BTNS);
        if (!btns.isEmpty()) btns.get(0).click();
        return this;
    }

    /** Добавить товар по индексу (0 = первый, 1 = второй...) */
    public InventoryPage addItemToCartByIndex(int index) {
        List<WebElement> btns = driver.findElements(ADD_TO_CART_BTNS);
        if (index < btns.size()) btns.get(index).click();
        return this;
    }

    /** Добавить конкретный товар по data-test id */
    public InventoryPage addItemToCart(String productId) {
        click(addToCartBtn(productId));
        return this;
    }

    /** Получить количество товаров в корзине (цифра на иконке) */
    public int getCartCount() {
        if (!isDisplayed(CART_BADGE)) return 0;
        return Integer.parseInt(getText(CART_BADGE));
    }

    /** Перейти в корзину */
    public CartPage goToCart() {
        click(CART_ICON);
        return new CartPage();
    }

    /** Выйти из системы */
   public LoginPage logout() {
    WebElement burgerBtn = driver.findElement(BURGER_MENU);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", burgerBtn);
    try { Thread.sleep(1500); } catch (InterruptedException e) {}
    WebElement logoutLink = waitVisible(LOGOUT_LINK);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutLink);
    try { Thread.sleep(2000); } catch (InterruptedException e) {}
    return new LoginPage();
}
    // ── ПРОВЕРКИ ─────────────────────────────────────────────────────────────

    /** Получить заголовок страницы каталога */
    public String getTitle() {
        return getText(PAGE_TITLE);
    }

    /** Мы на странице каталога? */
    public boolean isOnInventoryPage() {
        return getCurrentUrl().contains("inventory") && isDisplayed(INVENTORY_LIST);
    }

    /** Получить список названий всех товаров */
    public List<String> getProductNames() {
        return driver.findElements(PRODUCT_NAMES)
                     .stream()
                     .map(WebElement::getText)
                     .collect(Collectors.toList());
    }

    /** Получить количество товаров на странице */
    public int getProductCount() {
        return driver.findElements(PRODUCT_NAMES).size();
    }
}
