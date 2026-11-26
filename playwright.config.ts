import { defineConfig } from '@playwright/test';

export default defineConfig({
    testDir: './tests',

    // Тесты независимы друг от друга, поэтому запускаются параллельно.
    fullyParallel: true,

    reporter: [['list'], ['html', { open: 'never' }]],

    use: {
        // переопределить адрес
        baseURL: process.env.API_BASE_URL ?? 'https://dummyjson.com',
    },
});
