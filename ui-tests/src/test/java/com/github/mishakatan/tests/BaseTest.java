package com.github.mishakatan.tests;

import com.github.mishakatan.pages.LoginPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {
    private static final String BASE_URL = "https://www.saucedemo.com/";

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch();
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createPage() {
        context = browser.newContext(new Browser.NewContextOptions().setBaseURL(BASE_URL));
        page = context.newPage();
    }

    @AfterEach
    void closePage() {
        context.close();
    }

    protected LoginPage openLoginPage() {
        return new LoginPage(page).open();
    }
}
