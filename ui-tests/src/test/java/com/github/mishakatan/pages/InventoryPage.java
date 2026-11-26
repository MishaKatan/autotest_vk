package com.github.mishakatan.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class InventoryPage {
    private final Locator title;
    private final Locator productCards;
    private final Locator cartBadge;

    public InventoryPage(Page page) {
        this.title = page.getByTestId("title");
        this.productCards = page.getByTestId("inventory-item");
        this.cartBadge = page.getByTestId("shopping-cart-badge");
    }

    public void shouldBeOpened() {
        assertThat(title).hasText("Products");
    }

    public InventoryPage addToCart(String productName) {
        productCards
                .filter(new Locator.FilterOptions().setHasText(productName))
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to cart"))
                .click();
        return this;
    }

    public void shouldHaveCartItemsCount(int expectedCount) {
        assertThat(cartBadge).hasText(String.valueOf(expectedCount));
    }
}