package com.github.mishakatan.tests;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.github.mishakatan.data.User;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.mishakatan.data.User;

@DisplayName("Вход в магазин")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class LoginTest extends BaseTest {

        @ParameterizedTest(name = "Пользователь {0} может войти")
    @MethodSource("usersWhoCanLogIn")
    @Tag("smoke")
    void userCanLogIn(User user) {
        openLoginPage()
                .loginAs(user)
                .shouldBeOpened();
    }

    static Stream<User> usersWhoCanLogIn() {
        return Stream.of(User.STANDARD, User.PROBLEM, User.VISUAL);
    }

    @Test
    @Tag("negative")
    @DisplayName("Заблокированный пользователь видит ошибку")
    void lockedOutUserSeesError() {
        openLoginPage()
                .loginWithError(User.LOCKED_OUT)
                .shouldShowError("Epic sadface: Sorry, this user has been locked out.");
    }
}