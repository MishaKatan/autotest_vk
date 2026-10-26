import { test, expect } from '@playwright/test';
import { VALID_USER, JWT_FORMAT } from '../data/users';
import { AuthClient } from '../clients/AuthClient';
import { getTokens } from '../utils/auth';

test('TC-9. Refresh tokens with valid refresh token', async ({request, baseURL}) => {
    const {refreshToken} = await getTokens(baseURL);
    const auth = new AuthClient(request);

    const response = await auth.refresh(refreshToken);

    expect(response.status()).toBe(200);

    const body = await response.json();

    expect(body.accessToken).toMatch(JWT_FORMAT);
    expect(body.refreshToken).toMatch(JWT_FORMAT);

    const meResponse = await auth.me(body.accessToken);

    expect(meResponse.status()).toBe(200);
    expect((await meResponse.json()).username).toBe(VALID_USER.username);
});

const invalidRefreshCases = [
    { name: 'no token',      token: undefined, status: 401, message: 'Refresh token required' },
    { name: 'random string', token: 'garbage', status: 403, message: 'Invalid refresh token' },
];

for (const {name, token, status, message} of invalidRefreshCases){
    test(`TC-10. Refresh tokens with invalid refresh token: ${name}`, async ({request}) => {
        const auth = new AuthClient(request);
        const response = await auth.refresh(token);

        expect(response.status()).toBe(status);

        const body = await response.json();

        expect(body.message).toBe(message);
        expect(body).not.toHaveProperty('accessToken');
    });
}

test('TC-10. Refresh tokens with invalid refresh token: access token instead of refresh', async ({request,baseURL}) => {
    test.fail(true, 'BUG-9: access token is accepted as refresh token');

    const auth = new AuthClient(request);
    const {accessToken} = await getTokens(baseURL);
    const response = await auth.refresh(accessToken);

    expect(response.status()).toBe(403);

    const body = await response.json();

    expect(body.message).toBe('Invalid refresh token');
    expect(body).not.toHaveProperty('accessToken');
});