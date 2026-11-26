package com.github.mishakatan.pages;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public abstract BasePage waitUntilLoaded();
}