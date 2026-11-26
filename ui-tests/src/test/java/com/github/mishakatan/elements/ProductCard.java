package com.github.mishakatan.elements;

import com.microsoft.playwright.Locator;

public class ProductCard {
    private final Locator addToCartButton;

    public ProductCard(Locator root) {
        this.addToCartButton = root.locator("[data-test^='add-to-cart']");
    }

    public void addToCart() {
        addToCartButton.click();
    }
}
