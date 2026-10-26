import { APIRequestContext } from "@playwright/test";

export class AuthClient {
    constructor(private readonly request: APIRequestContext) {}

    login(body: object){
        return this.request.post('/auth/login', { data: body });
    }
    
    me(token?: string){
        return this.request.get('/auth/me', {
                headers: token ? {Authorization: `Bearer ${token}`} : {},
            });
    }

    refresh(refreshToken?: string){
        return this.request.post('/auth/refresh', {
            data: {refreshToken},
        });
    }
}