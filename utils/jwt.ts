export function tokenLifetimeMins (token: string): number{
    const payload = token.split('.')[1];
    const json = Buffer.from(payload, 'base64url').toString();
    const {iat, exp } = JSON.parse(json);

    return (exp - iat) / 60
}