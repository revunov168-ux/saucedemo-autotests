package com.saucedemo.config;

/**
 * Config.java
 * Все константы проекта в одном месте.
 * Если нужно что-то изменить (URL, логин, таймаут) — меняем только здесь.
 */
public class Config {

    // ── URL ──────────────────────────────────────────────────────────────────
    public static final String BASE_URL = "https://www.saucedemo.com";

    // ── ПОЛЬЗОВАТЕЛИ ─────────────────────────────────────────────────────────
    // У Sauce Demo 6 предустановленных пользователей с одним паролем
    public static final String PASSWORD = "secret_sauce";

    // Обычный пользователь — все функции работают нормально
    public static final String USER_STANDARD     = "standard_user";

    // Заблокированный — нельзя войти, показывает ошибку
    public static final String USER_LOCKED       = "locked_out_user";

    // Проблемный — некоторые элементы работают некорректно (баги намеренно)
    public static final String USER_PROBLEM      = "problem_user";

    // Медленный — всё работает, но страницы грузятся с задержкой
    public static final String USER_PERFORMANCE  = "performance_glitch_user";

    // Пользователь с ошибками — визуальные баги
    public static final String USER_ERROR        = "error_user";

    // Визуальный пользователь — визуальные отличия
    public static final String USER_VISUAL       = "visual_user";

    // Несуществующий — для негативных тестов
    public static final String USER_WRONG        = "wrong_user";

    // ── ТАЙМАУТЫ (секунды) ────────────────────────────────────────────────────
    public static final int TIMEOUT_IMPLICIT = 10;   // неявное ожидание
    public static final int TIMEOUT_EXPLICIT = 15;   // явное ожидание
    public static final int TIMEOUT_PAGE     = 20;   // загрузка страницы

    // ── ТЕСТОВЫЕ ДАННЫЕ — ОФОРМЛЕНИЕ ЗАКАЗА ──────────────────────────────────
    public static final String CHECKOUT_FIRST_NAME = "Dmitry";
    public static final String CHECKOUT_LAST_NAME  = "Revunov";
    public static final String CHECKOUT_ZIP        = "190000";

    // ── ПУТИ К ФАЙЛАМ ────────────────────────────────────────────────────────
    public static final String SCREENSHOTS_DIR  = "screenshots/";
    public static final String REPORTS_DIR      = "test-output/";
    public static final String REPORT_FILE      = REPORTS_DIR + "ExtentReport.html";
}
