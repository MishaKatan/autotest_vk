package com.github.mishakatan.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.github.mishakatan.data.User;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage extends BasePage{
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        super(page);
        this.usernameInput = page.getByTestId("username");
        this.passwordInput = page.getByTestId("password");
        this.loginButton = page.getByTestId("login-button");
        this.errorMessage = page.getByTestId("error");
    }

    public LoginPage open() {
        page.navigate("/");
        return waitUntilLoaded();
    }

    @Override
    public LoginPage waitUntilLoaded() {
        assertThat(loginButton).isVisible();
        return this;
    }

    public InventoryPage loginAs(User user) {
        submit(user);
        return new InventoryPage(page).waitUntilLoaded();
    }

    public LoginPage loginWithError(User user) {
        submit(user);
        return this;
    }

    public void shouldShowError(String expectedText) {
        assertThat(errorMessage).hasText(expectedText);
    }

    private void submit(User user) {
        usernameInput.fill(user.username());
        passwordInput.fill(user.password());
        loginButton.click();
    }
}