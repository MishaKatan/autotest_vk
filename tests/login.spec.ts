import {test, expect} from '@playwright/test';
import { VALID_USER, JWT_FORMAT, DEFAULT_EXPIRES_IN_MINS, MAX_EXPIRES_IN_MINS } from '../data/users';
import { AuthClient } from '../clients/AuthClient';
import { tokenLifetimeMins } from '../utils/jwt';

test('TC-1. Login with valid credentials', async ({ request }) => {
    const auth = new AuthClient(request);
    const response = await auth.login(VALID_USER);

    expect(response.status()).toBe(200);

    const body = await response.json();

    expect(body.username).toBe(VALID_USER.username);
    expect(body.id).toBe(VALID_USER.id);
    expect(body).not.toHaveProperty('password');

    expect(body.accessToken).toMatch(JWT_FORMAT);
    expect(body.refreshToken).toMatch(JWT_FORMAT);

    expect(tokenLifetimeMins(body.accessToken)).toBe(DEFAULT_EXPIRES_IN_MINS);
});

test('TC-2. Login with invalid password', async ({ request }) => {
    const auth = new AuthClient(request);
    const response = await auth.login({username: VALID_USER.username, password: 'wrongpassword'});

    expect(response.status()).toBe(400);

    const body = await response.json();

    expect(body.message).toBe('Invalid credentials');
});

test('TC-3. Login with invalid login', async ({ request }) => {
    const auth = new AuthClient(request);
    const response = await auth.login({username: 'wrongUser', password: VALID_USER.password});

    expect(response.status()).toBe(400);

    const body = await response.json();

    expect(body.message).toBe('Invalid credentials');
});

const emptyParamCases = [
    {name: 'empty body', body: {}},
    {name: 'empty username', body: {username: '', password: VALID_USER.password}},
    {name: 'empty password', body: {username: VALID_USER.username, password: ''}},
    {name: 'no username', body: {password: VALID_USER.password}},
    {name: 'no password', body: {username: VALID_USER.username}},
    {name: 'empty strings', body: {password: '', username: ''}},
];

for (const {name, body} of emptyParamCases) {
    test(`TC-4. Login with empty params: ${name}`, async ({ request }) => {
        const auth = new AuthClient(request);
        const response = await auth.login(body);

        expect(response.status()).toBe(400);

        const responseBody = await response.json();

        expect(responseBody.message).toBe('Username and password required');
    });
}

const validExpiresCases = [
    { name: 'minimum', expiresInMins: 1 },
    { name: 'maximum', expiresInMins: MAX_EXPIRES_IN_MINS },
];

for (const { name, expiresInMins } of validExpiresCases) {
    test(`TC-5. Login with valid expiresInMins: ${name} (${expiresInMins})`, async ({ request }) => {
        const auth = new AuthClient(request);
        const response = await auth.login({ ...VALID_USER, expiresInMins });

        expect(response.status()).toBe(200);

        const body = await response.json();

        expect(tokenLifetimeMins(body.accessToken)).toBe(expiresInMins);
    });
}

// bug — известный баг из docs/test-cases.md: тест помечается test.fail(). Когда баг исправят, поле очищается.
const invalidExpiresCases = [
    { name: 'above maximum', expiresInMins: MAX_EXPIRES_IN_MINS + 1, bug: 'BUG-2: returns 500 instead of 400' },
    { name: 'zero',          expiresInMins: 0,     bug: 'BUG-1: returns 200 and a token for 43200 minutes' },
    { name: 'negative',      expiresInMins: -5,    bug: 'BUG-3: returns 500 instead of 400' },
    { name: 'not a number',  expiresInMins: 'abc', bug: 'BUG-4: returns 500 instead of 400' },
];

for (const { name, expiresInMins, bug } of invalidExpiresCases) {
    test(`TC-5. Login with invalid expiresInMins: ${name} (${expiresInMins})`, async ({ request }) => {
        test.fail(bug !== '', bug);

        const auth = new AuthClient(request);
        const response = await auth.login({ ...VALID_USER, expiresInMins });

        expect(response.status()).toBe(400);

        const body = await response.json();

        expect(body.message).toEqual(expect.any(String));
        expect(body).not.toHaveProperty('accessToken');
    });
}

