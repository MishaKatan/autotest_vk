import {test, expect} from '@playwright/test';
import {VALID_USER} from '../data/users';
import { AuthClient } from '../clients/AuthClient';
import { getTokens } from '../utils/auth';

test('TC-6. Get current user with valid token', async ({request, baseURL}) => {
    const {accessToken} = await getTokens(baseURL);
    const auth = new AuthClient(request);
    const response = await auth.me(accessToken);

    expect(response.status()).toBe(200);

    const body = await response.json();

    expect(body.username).toBe(VALID_USER.username);
    expect(body.id).toBe(VALID_USER.id);
});

test('TC-6. Get current user with valid token: password is not exposed', async ({ request, baseURL }) => {
    test.fail(true, 'BUG-5: /auth/me returns user password in plain text');

    const { accessToken } = await getTokens(baseURL);

    const auth = new AuthClient(request);
    const response = await auth.me(accessToken);

    expect(response.status()).toBe(200);

    const body = await response.json();

    expect(body).not.toHaveProperty('password');
});


// bug — известный баг из docs/test-cases.md: тест помечается test.fail(). Когда баг исправят, поле очищается.
const invalidTokenCases = [
    {name: 'random string', token: 'garbage', bug: ''},
    {name: 'malformed JWT', token: 'abc.def.ghi', bug: 'BUG-6: malformed token returns 500 instead of 401'}
];

for (const { name, token, bug } of invalidTokenCases) {
    test(`TC-7. Get current user with invalid token: ${name}`, async ({ request }) => {
        test.fail(bug !== '', bug);

        const auth = new AuthClient(request);
        const response = await auth.me(token);

        expect(response.status()).toBe(401);

        const body = await response.json();

        expect(body.message).toEqual(expect.any(String));
        expect(body).not.toHaveProperty('username');
    });
}

test('TC-7. Get current user with invalid token: tampered signature', async ({ request, baseURL }) => {
    test.fail(true, 'BUG-7: tampered signature returns 500 instead of 401');

    const { accessToken } = await getTokens(baseURL);
    const tamperedToken = accessToken.slice(0, -5) + 'xxxxx';

    const auth = new AuthClient(request);
    const response = await auth.me(tamperedToken);

    expect(response.status()).toBe(401);

    const body = await response.json();

    expect(body.message).toEqual(expect.any(String));
    expect(body).not.toHaveProperty('username');
});

test('TC-7. Get current user with invalid token: refresh token instead of access', async ({ request, baseURL }) => {
    test.fail(true, 'BUG-8: refresh token is accepted as access token');

    const { refreshToken } = await getTokens(baseURL);

    const auth = new AuthClient(request);
    const response = await auth.me(refreshToken);

    expect(response.status()).toBe(401);

    const body = await response.json();

    expect(body.message).toEqual(expect.any(String));
    expect(body).not.toHaveProperty('username');
});

test('TC-8. Get current user without token', async ({request}) => {
    const auth = new AuthClient(request);
    const response = await auth.me();

    expect(response.status()).toBe(401);

    const body = await response.json();

    expect(body.message).toBe('Access Token is required');
});