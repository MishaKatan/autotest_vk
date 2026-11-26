package com.github.mishakatan.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.github.mishakatan.elements.ProductCard;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class InventoryPage extends BasePage {
    private final Locator title;
    private final Locator productCards;
    private final Locator cartBadge;
    private final Locator productList;

    public InventoryPage(Page page) {
        super(page);
        this.title = page.getByTestId("title");
        this.productCards = page.getByTestId("inventory-item");
        this.cartBadge = page.getByTestId("shopping-cart-badge");
        this.productList = page.getByTestId("inventory-list");
    }

    @Override
    public InventoryPage waitUntilLoaded() {
        assertThat(productList).isVisible();
        return this;
    }

    public void shouldBeOpened() {
        assertThat(title).hasText("Products");
    }

    public InventoryPage addToCart(String productName) {
        product(productName).addToCart();
        return this;
    }

    public void shouldHaveCartItemsCount(int expectedCount) {
        assertThat(cartBadge).hasText(String.valueOf(expectedCount));
    }

    public void shouldHaveProductsCount(int expectedCount) {
        assertThat(productCards).hasCount(expectedCount);
    }

    private ProductCard product(String productName) {
        Locator name = page.getByTestId("inventory-item-name")
                .filter(new Locator.FilterOptions().setHasText(productName));
        return new ProductCard(productCards.filter(new Locator.FilterOptions().setHas(name)));
    }
}