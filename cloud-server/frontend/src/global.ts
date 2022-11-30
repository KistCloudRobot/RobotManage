import {RestService} from "./rest-api";
// @ts-ignore
import {http} from "@/mdk/auth-api";

// @ts-ignore
import {AuthApi} from "@/mdk/auth-api.js";

/** MDK-OAuth 사용 시, AuthApi 를 상속한다. */
export const _service = new RestService();
export class Application extends AuthApi {
    private service: RestService;
    constructor(router: any) {
        super(router, "/login")
        this.service = _service;
    }

    async http_post(url: string, data: any) {
        const res = await http.post(url, data)
        return res.data;
    }

    async http_get(url: string, data: any) {
        const res = await http.get(url, data)
        return res.data;
    }
}

