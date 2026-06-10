package com.saucedemo.tests;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.utils.ReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.saucedemo.config.Config.*;

/**
 * CartTest.java
 * Тесты корзины для saucedemo.com.
 *
 * Покрытие:
 *   TC-CART-001  Добавить 1 товар — счётчик = 1           Критический
 *   TC-CART-002  Добавить несколько товаров               Высокий
 *   TC-CART-003  Товар отображается в корзине             Критический
 *   TC-CART-004  Удалить товар из корзины                 Критический
 *   TC-CART-005  Корзина пуста после удаления всех        Высокий
 *   TC-CART-006  Кнопка «Continue Shopping» → каталог    Средний
 *   TC-CART-007  Кнопка «Checkout» → оформление           Критический
 */
public class CartTest extends BaseTest {

    // ── TC-CART-001: Добавить 1 товар — счётчик = 1 ──────────────────────────
    @Test(priority = 1, description = "TC-CART-001: Добавить 1 товар в корзину")
    public void testAddOneItemToCart() {
        ReportManager.createTest("TC-CART-001", "Добавить 1 товар — счётчик корзины = 1");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Добавить первый товар в корзину");
        inventoryPage.addFirstItemToCart();

        int cartCount = inventoryPage.getCartCount();
        Assert.assertEquals(cartCount, 1,
            "❌ Счётчик корзины должен быть 1, получено: " + cartCount);

        logPass("Товар добавлен. Счётчик корзины: " + cartCount);
    }

    // ── TC-CART-002: Добавить несколько товаров ───────────────────────────────
    @Test(priority = 2, description = "TC-CART-002: Добавить 3 товара в корзину")
    public void testAddMultipleItemsToCart() {
        ReportManager.createTest("TC-CART-002", "Добавить 3 товара — счётчик корзины = 3");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Добавить 3 товара по очереди");
        inventoryPage.addItemToCartByIndex(0);
        inventoryPage.addItemToCartByIndex(1);
        inventoryPage.addItemToCartByIndex(2);

        int cartCount = inventoryPage.getCartCount();
        Assert.assertEquals(cartCount, 3,
            "❌ Счётчик корзины должен быть 3, получено: " + cartCount);

        logPass("3 товара добавлены. Счётчик корзины: " + cartCount);
    }

    // ── TC-CART-003: Товар отображается в корзине ─────────────────────────────
    @Test(priority = 3, description = "TC-CART-003: Добавленный товар отображается в корзине")
    public void testItemAppearsInCart() {
        ReportManager.createTest("TC-CART-003", "Товар отображается в корзине после добавления");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Запомнить название первого товара");
        String firstProductName = inventoryPage.getProductNames().get(0);
        logStep("Первый товар: " + firstProductName);

        logStep("Добавить первый товар в корзину");
        inventoryPage.addFirstItemToCart();

        logStep("Перейти в корзину");
        CartPage cartPage = inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isOnCartPage(),
            "❌ Не перешли на страницу корзины. URL: " + cartPage.getCurrentUrl());

        Assert.assertEquals(cartPage.getItemCount(), 1,
            "❌ В корзине должен быть 1 товар, получено: " + cartPage.getItemCount());

        Assert.assertTrue(cartPage.containsItem(firstProductName),
            "❌ В корзине нет товара '" + firstProductName + "'. "
            + "Товары в корзине: " + cartPage.getItemNames());

        logPass("Товар '" + firstProductName + "' отображается в корзине");
    }

    // ── TC-CART-004: Удалить товар из корзины ────────────────────────────────
    @Test(priority = 4, description = "TC-CART-004: Удалить товар из корзины")
    public void testRemoveItemFromCart() {
        ReportManager.createTest("TC-CART-004", "Удаление товара из корзины");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Добавить 2 товара в корзину");
        inventoryPage.addItemToCartByIndex(0);
        inventoryPage.addItemToCartByIndex(1);

        Assert.assertEquals(inventoryPage.getCartCount(), 2,
            "❌ Должно быть 2 товара перед удалением");

        logStep("Перейти в корзину");
        CartPage cartPage = inventoryPage.goToCart();

        logStep("Удалить первый товар");
        cartPage.removeFirstItem();

        Assert.assertEquals(cartPage.getItemCount(), 1,
            "❌ После удаления должен остаться 1 товар, получено: " + cartPage.getItemCount());

        logPass("Товар удалён. В корзине осталось: " + cartPage.getItemCount());
    }

    // ── TC-CART-005: Корзина пуста после удаления всех ───────────────────────
    @Test(priority = 5, description = "TC-CART-005: Корзина пуста после удаления всех товаров")
    public void testCartEmptyAfterRemoveAll() {
        ReportManager.createTest("TC-CART-005", "Корзина пуста после удаления всех товаров");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Добавить 1 товар");
        inventoryPage.addFirstItemToCart();
        Assert.assertEquals(inventoryPage.getCartCount(), 1);

        logStep("Перейти в корзину");
        CartPage cartPage = inventoryPage.goToCart();

        logStep("Удалить единственный товар");
        cartPage.removeFirstItem();

        Assert.assertTrue(cartPage.isEmpty(),
            "❌ Корзина не пуста после удаления всех товаров. "
            + "Осталось: " + cartPage.getItemCount());

        logPass("Корзина пуста после удаления всех товаров");
    }

    // ── TC-CART-006: «Continue Shopping» возвращает в каталог ────────────────
    @Test(priority = 6, description = "TC-CART-006: Кнопка Continue Shopping → каталог")
    public void testContinueShopping() {
        ReportManager.createTest("TC-CART-006", "Continue Shopping возвращает в каталог");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Перейти в корзину");
        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertTrue(cartPage.isOnCartPage(), "❌ Не открылась страница корзины");

        logStep("Нажать Continue Shopping");
        InventoryPage backToInventory = cartPage.continueShopping();

        Assert.assertTrue(backToInventory.isOnInventoryPage(),
            "❌ После Continue Shopping не вернулись в каталог. "
            + "URL: " + backToInventory.getCurrentUrl());

        logPass("Continue Shopping вернул в каталог. URL: " + backToInventory.getCurrentUrl());
    }

    // ── TC-CART-007: Кнопка «Checkout» → оформление заказа ──────────────────
    @Test(priority = 7, description = "TC-CART-007: Кнопка Checkout открывает оформление заказа")
    public void testCheckoutButtonNavigation() {
        ReportManager.createTest("TC-CART-007", "Кнопка Checkout → страница оформления");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        logStep("Добавить товар в корзину");
        inventoryPage.addFirstItemToCart();

        logStep("Перейти в корзину");
        CartPage cartPage = inventoryPage.goToCart();

        logStep("Нажать Checkout");
        com.saucedemo.pages.CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        Assert.assertTrue(checkoutPage.isOnStep1(),
            "❌ После Checkout не открылся шаг 1 оформления. "
            + "URL: " + checkoutPage.getCurrentUrl());

        logPass("Checkout открыл шаг 1 оформления. URL: " + checkoutPage.getCurrentUrl());
    }
}
