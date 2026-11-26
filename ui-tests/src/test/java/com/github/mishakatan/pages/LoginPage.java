package com.github.mishakatan.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        this.page = page;
        this.usernameInput = page.getByTestId("username");
        this.passwordInput = page.getByTestId("password");
        this.loginButton = page.getByTestId("login-button");
        this.errorMessage = page.getByTestId("error");
    }

    public LoginPage open() {
        page.navigate("/");
        return this;
    }

    public InventoryPage loginAs(String username, String password) {
        submit(username, password);
        return new InventoryPage(page);
    }

    public LoginPage loginWithError(String username, String password) {
        submit(username, password);
        return this;
    }

    public void shouldShowError(String expectedText) {
        assertThat(errorMessage).hasText(expectedText);
    }

    private void submit(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}