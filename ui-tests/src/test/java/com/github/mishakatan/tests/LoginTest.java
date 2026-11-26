package com.github.mishakatan.tests;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Вход в магазин")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "Пользователь {0} может войти")
    @ValueSource(strings = {"standard_user", "problem_user", "visual_user"})
    @Tag("smoke")
    void userCanLogIn(String username) {
        openLoginPage()
                .loginAs(username, "secret_sauce")
                .shouldBeOpened();
    }

    @Test
    @Tag("negative")
    @DisplayName("Заблокированный пользователь видит ошибку")
    void lockedOutUserSeesError() {
        openLoginPage()
                .loginWithError("locked_out_user", "secret_sauce")
                .shouldShowError("Epic sadface: Sorry, this user has been locked out.");
    }
}