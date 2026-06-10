package com.saucedemo.tests;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.utils.ReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.saucedemo.config.Config.*;

/**
 * CheckoutTest.java
 * Тесты оформления заказа для saucedemo.com.
 *
 * Покрытие:
 *   TC-CHK-001  Полный цикл заказа от входа до подтверждения  Критический
 *   TC-CHK-002  Пустое поле First Name — ошибка валидации      Высокий
 *   TC-CHK-003  Пустое поле Last Name — ошибка валидации       Высокий
 *   TC-CHK-004  Пустое поле Zip — ошибка валидации             Высокий
 *   TC-CHK-005  Все поля пустые — ошибка валидации             Высокий
 *   TC-CHK-006  Обзор заказа содержит правильные данные        Средний
 *   TC-CHK-007  Кнопка Cancel на шаге 1 → возврат в корзину   Средний
 */
public class CheckoutTest extends BaseTest {

    /**
     * Вспомогательный метод: войти и добавить товар в корзину.
     * Используется как предусловие в большинстве тестов.
     */
    private CartPage loginAndAddToCart() {
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);
        inventoryPage.addItemToCartByIndex(0);
        inventoryPage.addItemToCartByIndex(1);
        return inventoryPage.goToCart();
    }

    // ── TC-CHK-001: Полный цикл заказа ───────────────────────────────────────
    @Test(priority = 1, description = "TC-CHK-001: Полный цикл — от входа до подтверждения")
    public void testFullCheckoutFlow() {
        ReportManager.createTest("TC-CHK-001", "Полный цикл заказа от входа до подтверждения");

        // Шаг 1: Вход и добавление товаров
        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);
        Assert.assertTrue(inventoryPage.isOnInventoryPage(), "❌ Вход не выполнен");

        logStep("Добавить 2 товара в корзину");
        inventoryPage.addItemToCartByIndex(0);
        inventoryPage.addItemToCartByIndex(1);
        Assert.assertEquals(inventoryPage.getCartCount(), 2, "❌ Неверный счётчик корзины");
        logPass("2 товара добавлены. Счётчик: 2");

        // Шаг 2: Переход в корзину
        logStep("Перейти в корзину");
        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertTrue(cartPage.isOnCartPage(), "❌ Корзина не открылась");
        Assert.assertEquals(cartPage.getItemCount(), 2, "❌ В корзине не 2 товара");
        logPass("В корзине 2 товара");

        // Шаг 3: Начало оформления
        logStep("Нажать Checkout");
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isOnStep1(), "❌ Шаг 1 не открылся");
        logPass("Шаг 1: форма данных покупателя");

        // Шаг 4: Заполнение данных покупателя
        logStep("Заполнить: " + CHECKOUT_FIRST_NAME + " " + CHECKOUT_LAST_NAME
                + ", индекс: " + CHECKOUT_ZIP);
        checkoutPage.fillShippingInfo(
            CHECKOUT_FIRST_NAME,
            CHECKOUT_LAST_NAME,
            CHECKOUT_ZIP
        );
        Assert.assertTrue(checkoutPage.isOnStep2(), "❌ Шаг 2 не открылся после заполнения");
        logPass("Шаг 2: обзор заказа открыт");

        // Шаг 5: Проверка итогов
        String total = checkoutPage.getTotal();
        logStep("Итоговая сумма: " + total);
        Assert.assertFalse(total.isEmpty(), "❌ Итоговая сумма не отображается");
        Assert.assertEquals(checkoutPage.getSummaryItemCount(), 2,
            "❌ В обзоре заказа не 2 товара");
        logPass("Обзор заказа корректен. Итого: " + total);

        // Шаг 6: Завершение заказа
        logStep("Нажать Finish для подтверждения заказа");
        checkoutPage.clickFinish();
        Assert.assertTrue(checkoutPage.isOnCompletePage(),
            "❌ Страница подтверждения не открылась. URL: " + checkoutPage.getCurrentUrl());

        String completeHeader = checkoutPage.getCompleteHeader();
        Assert.assertEquals(completeHeader, "Thank you for your order!",
            "❌ Заголовок подтверждения не совпадает. Получено: '" + completeHeader + "'");

        logPass("Заказ оформлен! Заголовок: '" + completeHeader + "'");
    }

    // ── TC-CHK-002: Пустое поле First Name ───────────────────────────────────
    @Test(priority = 2, description = "TC-CHK-002: Пустое поле First Name — ошибка валидации")
    public void testCheckoutEmptyFirstName() {
        ReportManager.createTest("TC-CHK-002", "Пустое First Name — ошибка валидации");

        logStep("Войти, добавить товары, перейти к оформлению");
        CartPage cartPage = loginAndAddToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        logStep("Оставить First Name пустым, заполнить остальные поля");
        checkoutPage.enterLastName(CHECKOUT_LAST_NAME);
        checkoutPage.enterZip(CHECKOUT_ZIP);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isOnStep1(),
            "❌ Форма прошла с пустым First Name (не должна была)");

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
            "❌ Ошибка валидации не отображается при пустом First Name");

        String error = checkoutPage.getErrorMessage();
        Assert.assertTrue(error.toLowerCase().contains("first name"),
            "❌ Ошибка не упоминает First Name. Получено: '" + error + "'");

        logPass("Валидация сработала. Ошибка: '" + error + "'");
    }

    // ── TC-CHK-003: Пустое поле Last Name ────────────────────────────────────
    @Test(priority = 3, description = "TC-CHK-003: Пустое поле Last Name — ошибка валидации")
    public void testCheckoutEmptyLastName() {
        ReportManager.createTest("TC-CHK-003", "Пустое Last Name — ошибка валидации");

        logStep("Войти, добавить товары, перейти к оформлению");
        CartPage cartPage = loginAndAddToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        logStep("Заполнить First Name и Zip, оставить Last Name пустым");
        checkoutPage.enterFirstName(CHECKOUT_FIRST_NAME);
        checkoutPage.enterZip(CHECKOUT_ZIP);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
            "❌ Ошибка не появилась при пустом Last Name");

        String error = checkoutPage.getErrorMessage();
        Assert.assertTrue(error.toLowerCase().contains("last name"),
            "❌ Ошибка не упоминает Last Name. Получено: '" + error + "'");

        logPass("Валидация сработала. Ошибка: '" + error + "'");
    }

    // ── TC-CHK-004: Пустое поле Zip ──────────────────────────────────────────
    @Test(priority = 4, description = "TC-CHK-004: Пустое поле Postal Code — ошибка валидации")
    public void testCheckoutEmptyZip() {
        ReportManager.createTest("TC-CHK-004", "Пустой Postal Code — ошибка валидации");

        logStep("Войти, добавить товары, перейти к оформлению");
        CartPage cartPage = loginAndAddToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        logStep("Заполнить First Name и Last Name, оставить Zip пустым");
        checkoutPage.enterFirstName(CHECKOUT_FIRST_NAME);
        checkoutPage.enterLastName(CHECKOUT_LAST_NAME);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
            "❌ Ошибка не появилась при пустом Postal Code");

        String error = checkoutPage.getErrorMessage();
        Assert.assertTrue(
            error.toLowerCase().contains("postal") || error.toLowerCase().contains("zip"),
            "❌ Ошибка не упоминает Postal Code. Получено: '" + error + "'");

        logPass("Валидация сработала. Ошибка: '" + error + "'");
    }

    // ── TC-CHK-005: Все поля пустые ──────────────────────────────────────────
    @Test(priority = 5, description = "TC-CHK-005: Все поля пустые — ошибка валидации")
    public void testCheckoutAllFieldsEmpty() {
        ReportManager.createTest("TC-CHK-005", "Все поля пустые — ошибка валидации");

        logStep("Войти, добавить товары, перейти к оформлению");
        CartPage cartPage = loginAndAddToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        logStep("Не заполнять ни одного поля, нажать Continue");
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isOnStep1(),
            "❌ Форма прошла с пустыми полями (не должна была)");

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
            "❌ Ошибка не появилась при всех пустых полях");

        logPass("Форма не прошла. Ошибка: '" + checkoutPage.getErrorMessage() + "'");
    }

    // ── TC-CHK-006: Обзор заказа содержит правильные данные ──────────────────
    @Test(priority = 6, description = "TC-CHK-006: Обзор заказа — проверка данных")
    public void testCheckoutSummaryData() {
        ReportManager.createTest("TC-CHK-006", "Обзор заказа содержит корректные данные");

        logStep("Войти и добавить 1 товар");
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);
        String productName = inventoryPage.getProductNames().get(0);
        inventoryPage.addFirstItemToCart();

        logStep("Перейти в корзину → Checkout");
        CartPage cartPage = inventoryPage.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        logStep("Заполнить данные покупателя");
        checkoutPage.fillShippingInfo(CHECKOUT_FIRST_NAME, CHECKOUT_LAST_NAME, CHECKOUT_ZIP);

        Assert.assertTrue(checkoutPage.isOnStep2(), "❌ Не перешли на шаг 2");

        logStep("Проверить наличие сумм в обзоре");
        String subtotal = checkoutPage.getSubtotal();
        String tax      = checkoutPage.getTax();
        String total    = checkoutPage.getTotal();

        Assert.assertFalse(subtotal.isEmpty(), "❌ Подытог не отображается");
        Assert.assertFalse(tax.isEmpty(),      "❌ Налог не отображается");
        Assert.assertFalse(total.isEmpty(),    "❌ Итого не отображается");

        Assert.assertEquals(checkoutPage.getSummaryItemCount(), 1,
            "❌ В обзоре должен быть 1 товар");

        logPass("Обзор заказа корректен:");
        logPass("  Подытог: " + subtotal);
        logPass("  Налог:   " + tax);
        logPass("  Итого:   " + total);
        logPass("  Товаров: " + checkoutPage.getSummaryItemCount());
    }

    // ── TC-CHK-007: Cancel на шаге 1 ─────────────────────────────────────────
    @Test(priority = 7, description = "TC-CHK-007: Cancel на шаге 1 — возврат в корзину")
    public void testCheckoutCancelOnStep1() {
        ReportManager.createTest("TC-CHK-007", "Cancel на шаге 1 → возврат в корзину");

        logStep("Войти, добавить товар, перейти к оформлению");
        CartPage cartPage = loginAndAddToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        Assert.assertTrue(checkoutPage.isOnStep1(), "❌ Шаг 1 не открылся");

        logStep("Нажать Cancel на шаге 1");
        CartPage returnedCart = checkoutPage.clickCancel();

        Assert.assertTrue(returnedCart.isOnCartPage(),
            "❌ После Cancel не вернулись в корзину. "
            + "URL: " + returnedCart.getCurrentUrl());

        Assert.assertEquals(returnedCart.getItemCount(), 2,
            "❌ Товары исчезли из корзины после отмены");

        logPass("Cancel вернул в корзину. Товары сохранены: " + returnedCart.getItemCount());
    }
}
