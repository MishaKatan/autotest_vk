package com.github.mishakatan.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

public class ProductCard {
    private final Locator addToCartButton;

    public ProductCard(Locator root) {
        this.addToCartButton = root.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Add to cart"));
    }

    public void addToCart() {
        addToCartButton.click();
    }
}