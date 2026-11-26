package com.github.mishakatan.tests;

import org.junit.jupiter.api.Test;

class InventoryTest extends BaseTest {

    @Test
    void addedProductIsShownInCartBadge() {
        openLoginPage()
                .loginAs("standard_user", "secret_sauce")
                .addToCart("Sauce Labs Backpack")
                .shouldHaveCartItemsCount(1);
    }
}