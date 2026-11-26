package com.github.mishakatan.tests;

import org.junit.jupiter.api.Test;

class LoginTest extends BaseTest {

    @Test
    void standardUserCanLogIn() {
        openLoginPage()
                .loginAs("standard_user", "secret_sauce")
                .shouldBeOpened();
    }

    @Test
    void lockedOutUserSeesError() {
        openLoginPage()
                .loginWithError("locked_out_user", "secret_sauce")
                .shouldShowError("Epic sadface: Sorry, this user has been locked out.");
    }
}