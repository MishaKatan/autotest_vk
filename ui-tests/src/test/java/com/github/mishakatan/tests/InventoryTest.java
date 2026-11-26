package com.github.mishakatan.tests;

import com.github.mishakatan.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Каталог товаров")
class InventoryTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeEach
    void logIn() {
        inventory = openLoginPage().loginAs("standard_user", "secret_sauce");
    }

    @Test
    @Tag("smoke")
    @DisplayName("Каталог открыт и в нём 6 товаров")
    void catalogShowsAllProducts() {
        assertAll(
                () -> inventory.shouldBeOpened(),
                () -> inventory.shouldHaveProductsCount(6)
        );
    }

    @Nested
    @DisplayName("Корзина")
    class Cart {

        @Test
        @Tag("smoke")
        @DisplayName("Добавленный товар виден на значке корзины")
        void addedProductIsShownInCartBadge() {
            inventory.addToCart("Sauce Labs Backpack")
                    .shouldHaveCartItemsCount(1);
        }

        @TestFactory
        @DisplayName("Счётчик корзины растёт с каждым товаром")
        Stream<DynamicTest> cartCounterGrows() {
            List<String> products = List.of(
                    "Sauce Labs Backpack",
                    "Sauce Labs Bike Light",
                    "Sauce Labs Onesie"
            );
            return IntStream.range(0, products.size())
                    .mapToObj(i -> DynamicTest.dynamicTest(
                            "Добавлен товар: " + products.get(i),
                            () -> inventory.addToCart(products.get(i))
                                    .shouldHaveCartItemsCount(i + 1)
                    ));
        }

        @Test
        @Disabled("Известный баг: problem_user не может добавить Bolt T-Shirt в корзину")
        @DisplayName("problem_user добавляет Bolt T-Shirt в корзину")
        void problemUserCanAddBoltTShirt() {
            openLoginPage()
                    .loginAs("problem_user", "secret_sauce")
                    .addToCart("Sauce Labs Bolt T-Shirt")
                    .shouldHaveCartItemsCount(1);
        }
    }
}