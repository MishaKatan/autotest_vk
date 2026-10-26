import { expect, request } from '@playwright/test';
import { AuthClient } from '../clients/AuthClient';
import { VALID_USER } from '../data/users';

// Логинимся в отдельном, сразу закрываемом контексте: при входе DummyJSON ставит cookie accessToken,
// и /auth/me принимает её. Если логиниться через фикстуру request, проверки «с токеном»
// проходили бы по cookie, даже если заголовок Authorization не работает.
export async function getTokens(baseURL?: string) {
    const context = await request.newContext({ baseURL });

    try {
        const auth = new AuthClient(context);
        const response = await auth.login(VALID_USER);

        expect(response.status(), 'Precondition failed: could not log in').toBe(200);

        const body = await response.json();

        return { accessToken: body.accessToken, refreshToken: body.refreshToken };
    } finally {
        await context.dispose();
    }
}
