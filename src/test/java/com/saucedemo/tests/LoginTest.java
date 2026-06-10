package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.utils.ReportManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.saucedemo.config.Config.*;

/**
 * LoginTest.java
 * Тесты авторизации для saucedemo.com.
 *
 * Покрытие:
 *   TC-LOGIN-001  Вход standard_user                  Критический
 *   TC-LOGIN-002  Вход locked_out_user — блокировка   Критический
 *   TC-LOGIN-003  Вход performance_glitch_user        Высокий
 *   TC-LOGIN-004  Пустой логин                        Высокий
 *   TC-LOGIN-005  Пустой пароль                       Высокий
 *   TC-LOGIN-006  Оба поля пустые                     Высокий
 *   TC-LOGIN-007  Неверный логин                      Высокий
 *   TC-LOGIN-008  Неверный пароль                     Высокий
 *   TC-LOGIN-009  Выход из системы                    Критический
 *   TC-LOGIN-010  Все 6 ролей (параметризованный)     Средний
 */
public class LoginTest extends BaseTest {

    // ── TC-LOGIN-001: Успешный вход standard_user ─────────────────────────────
    @Test(priority = 1, description = "TC-LOGIN-001: Успешный вход standard_user")
    public void testLoginStandardUser() {
        ReportManager.createTest("TC-LOGIN-001", "Вход с корректными данными standard_user");

        logStep("Открыть saucedemo.com");
        logStep("Ввести логин: " + USER_STANDARD);
        logStep("Ввести пароль: secret_sauce");
        logStep("Нажать Login");

        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        Assert.assertTrue(inventoryPage.isOnInventoryPage(),
            "❌ После входа страница каталога не открылась. URL: "
            + inventoryPage.getCurrentUrl());

        Assert.assertEquals(inventoryPage.getTitle(), "Products",
            "❌ Заголовок страницы не 'Products'");

        Assert.assertTrue(inventoryPage.getProductCount() > 0,
            "❌ На странице нет товаров");

        logPass("Вход выполнен. Страница каталога открыта. "
                + "Товаров: " + inventoryPage.getProductCount());
    }

    // ── TC-LOGIN-002: Заблокированный пользователь ────────────────────────────
    @Test(priority = 2, description = "TC-LOGIN-002: Вход locked_out_user — ожидается блокировка")
    public void testLoginLockedOutUser() {
        ReportManager.createTest("TC-LOGIN-002", "Вход заблокированного пользователя");

        logStep("Ввести логин: " + USER_LOCKED);
        logStep("Ввести пароль: secret_sauce");
        logStep("Нажать Login — ожидаем ошибку блокировки");

        loginPage.attemptLogin(USER_LOCKED, PASSWORD);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "❌ Сообщение об ошибке не появилось для заблокированного пользователя");

        String errorText = loginPage.getErrorMessage();
        Assert.assertTrue(
            errorText.toLowerCase().contains("locked"),
            "❌ Текст ошибки не содержит 'locked'. Получено: '" + errorText + "'"
        );

        Assert.assertTrue(loginPage.isOnLoginPage(),
            "❌ Заблокированный пользователь попал в систему!");

        logPass("Заблокированный пользователь не вошёл. Ошибка: '" + errorText + "'");
    }

    // ── TC-LOGIN-003: performance_glitch_user — медленная загрузка ────────────
    @Test(priority = 3, description = "TC-LOGIN-003: Вход performance_glitch_user")
    public void testLoginPerformanceUser() {
        ReportManager.createTest("TC-LOGIN-003", "Вход performance_glitch_user (медленная загрузка)");

        logStep("Ввести логин: " + USER_PERFORMANCE);
        logStep("Ввести пароль: secret_sauce");
        logStep("Нажать Login — страница загрузится с задержкой");

        long startTime = System.currentTimeMillis();
        InventoryPage inventoryPage = loginPage.login(USER_PERFORMANCE, PASSWORD);
        long elapsed = System.currentTimeMillis() - startTime;

        Assert.assertTrue(inventoryPage.isOnInventoryPage(),
            "❌ performance_glitch_user не вошёл в систему");

        logStep("Время загрузки: " + elapsed + " мс");

        // У этого пользователя намеренно медленная загрузка
        // Фиксируем факт замедления как информационный шаг
        if (elapsed > 3000) {
            logStep("⚠ Замечено замедление: " + elapsed + " мс (ожидаемое поведение для этой роли)");
        }

        logPass("performance_glitch_user вошёл. Время: " + elapsed + " мс");
    }

    // ── TC-LOGIN-004: Пустой логин ────────────────────────────────────────────
    @Test(priority = 4, description = "TC-LOGIN-004: Попытка входа с пустым логином")
    public void testLoginEmptyUsername() {
        ReportManager.createTest("TC-LOGIN-004", "Пустой логин — ожидается ошибка валидации");

        logStep("Оставить поле Username пустым");
        logStep("Ввести пароль: secret_sauce");
        logStep("Нажать Login");

        loginPage.attemptLogin("", PASSWORD);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "❌ Ошибка валидации не появилась при пустом логине");

        String errorText = loginPage.getErrorMessage();
        Assert.assertTrue(
            errorText.toLowerCase().contains("username"),
            "❌ Ошибка не упоминает поле username. Получено: '" + errorText + "'"
        );

        logPass("Валидация сработала. Ошибка: '" + errorText + "'");
    }

    // ── TC-LOGIN-005: Пустой пароль ───────────────────────────────────────────
    @Test(priority = 5, description = "TC-LOGIN-005: Попытка входа с пустым паролем")
    public void testLoginEmptyPassword() {
        ReportManager.createTest("TC-LOGIN-005", "Пустой пароль — ожидается ошибка валидации");

        logStep("Ввести логин: " + USER_STANDARD);
        logStep("Оставить поле Password пустым");
        logStep("Нажать Login");

        loginPage.attemptLogin(USER_STANDARD, "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "❌ Ошибка валидации не появилась при пустом пароле");

        String errorText = loginPage.getErrorMessage();
        Assert.assertTrue(
            errorText.toLowerCase().contains("password"),
            "❌ Ошибка не упоминает поле password. Получено: '" + errorText + "'"
        );

        logPass("Валидация сработала. Ошибка: '" + errorText + "'");
    }

    // ── TC-LOGIN-006: Оба поля пустые ─────────────────────────────────────────
    @Test(priority = 6, description = "TC-LOGIN-006: Оба поля пустые")
    public void testLoginBothFieldsEmpty() {
        ReportManager.createTest("TC-LOGIN-006", "Оба поля пустые — ожидается ошибка валидации");

        logStep("Оставить оба поля пустыми");
        logStep("Нажать Login");

        loginPage.attemptLogin("", "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "❌ Ошибка не появилась при полностью пустой форме");

        logPass("Форма не отправлена. Ошибка: '" + loginPage.getErrorMessage() + "'");
    }

    // ── TC-LOGIN-007: Неверный логин ──────────────────────────────────────────
    @Test(priority = 7, description = "TC-LOGIN-007: Неверный логин")
    public void testLoginWrongUsername() {
        ReportManager.createTest("TC-LOGIN-007", "Неверный логин — ожидается ошибка");

        logStep("Ввести несуществующий логин: " + USER_WRONG);
        logStep("Ввести пароль: secret_sauce");
        logStep("Нажать Login");

        loginPage.attemptLogin(USER_WRONG, PASSWORD);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "❌ Ошибка не появилась при неверном логине");

        Assert.assertFalse(
            loginPage.getCurrentUrl().contains("inventory"),
            "❌ Неверный пользователь попал в каталог!"
        );

        logPass("Вход отклонён. Ошибка: '" + loginPage.getErrorMessage() + "'");
    }

    // ── TC-LOGIN-008: Неверный пароль ─────────────────────────────────────────
    @Test(priority = 8, description = "TC-LOGIN-008: Неверный пароль")
    public void testLoginWrongPassword() {
        ReportManager.createTest("TC-LOGIN-008", "Неверный пароль — ожидается ошибка");

        logStep("Ввести логин: " + USER_STANDARD);
        logStep("Ввести неверный пароль: wrong_password");
        logStep("Нажать Login");

        loginPage.attemptLogin(USER_STANDARD, "wrong_password");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
            "❌ Ошибка не появилась при неверном пароле");

        logPass("Вход отклонён. Ошибка: '" + loginPage.getErrorMessage() + "'");
    }

    // ── TC-LOGIN-009: Выход из системы ────────────────────────────────────────
    @Test(priority = 9, description = "TC-LOGIN-009: Успешный выход из системы")
    public void testLogout() {
        ReportManager.createTest("TC-LOGIN-009", "Выход из системы — проверка сессии");

        logStep("Войти как " + USER_STANDARD);
        InventoryPage inventoryPage = loginPage.login(USER_STANDARD, PASSWORD);

        Assert.assertTrue(inventoryPage.isOnInventoryPage(), "❌ Вход не выполнен");
        logStep("Вход выполнен. URL: " + inventoryPage.getCurrentUrl());

        logStep("Нажать меню → Logout");
        loginPage = inventoryPage.logout();

        Assert.assertTrue(loginPage.isOnLoginPage(),
            "❌ После выхода страница входа не отображается. URL: "
            + loginPage.getCurrentUrl());

        logPass("Выход выполнен. Страница входа отображается.");

        // Дополнительно: URL не должен содержать inventory
        Assert.assertFalse(
            loginPage.getCurrentUrl().contains("inventory"),
            "❌ URL всё ещё содержит 'inventory' после выхода"
        );
        logPass("URL корректный после выхода: " + loginPage.getCurrentUrl());
    }

    // ── TC-LOGIN-010: Параметризованный — все пользователи ───────────────────
    @DataProvider(name = "allUsers")
    public Object[][] allUsersProvider() {
        return new Object[][] {
            // { логин,              ожидаем вход, описание }
            { USER_STANDARD,    true,  "standard_user — должен войти"         },
            { USER_LOCKED,      false, "locked_out_user — должен быть отказ"  },
            { USER_PROBLEM,     true,  "problem_user — должен войти"          },
            { USER_PERFORMANCE, true,  "performance_glitch_user — должен войти" },
            { USER_ERROR,       true,  "error_user — должен войти"            },
            { USER_VISUAL,      true,  "visual_user — должен войти"           },
        };
    }

    @Test(
        priority    = 10,
        dataProvider = "allUsers",
        description  = "TC-LOGIN-010: Проверка входа для всех 6 ролей"
    )
    public void testAllUsersLogin(String username, boolean expectSuccess, String description) {
        ReportManager.createTest(
            "TC-LOGIN-010 [" + username + "]",
            "Проверка роли: " + description
        );

        logStep("Логин: " + username + " | Пароль: secret_sauce");
        logStep("Ожидается: " + (expectSuccess ? "успешный вход" : "отказ"));

        loginPage.attemptLogin(username, PASSWORD);

        if (expectSuccess) {
            InventoryPage inv = new com.saucedemo.pages.InventoryPage();
            Assert.assertTrue(inv.isOnInventoryPage(),
                "❌ [" + username + "] не вошёл, хотя должен был. "
                + "URL: " + inv.getCurrentUrl());
            logPass("[" + username + "] успешно вошёл → " + description);
        } else {
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                "❌ [" + username + "] вошёл, хотя должен быть заблокирован!");
            logPass("[" + username + "] правильно заблокирован → " + description);
        }
    }
}
