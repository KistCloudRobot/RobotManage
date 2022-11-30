
const unsecured_http = require('axios');

const ID_TOKEN_KEY = "jwt_token"

export let http = unsecured_http;
let g_userInfo = {};
let g_access_token= null;
let g_loginHandler = null;

function decodeToken(jwt) {
    try {
        let base64Url = jwt.split('.')[1]
        let base64 = base64Url.replace('-', '+').replace('_', '/');
        let jsonToken = JSON.parse(window.atob(base64));
        return jsonToken;
    }
    catch (e) {
        console.log(e);
        return null;
    }
}

function createHttpConnection() {
    g_access_token = getToken();

    if (g_access_token) {
        g_userInfo = decodeToken(g_access_token);
    }

    if (g_userInfo != null) {
        if (g_loginHandler != null) {
            g_loginHandler.setWebToken(g_userInfo);
        }

        http = unsecured_http.create({
            headers: { "Authorization": 'Bearer ' + g_access_token }
        })
    }
    else {
        http = unsecured_http;
    }

    http.interceptors.response.use(function (response) {
        // Any status code that lie within the range of 2xx cause this function to trigger
        // Do something with response data
        return response;
    }, function (error) {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        // Do something with response error
        console.log('reject!!!!!!!!!!!', error)
        if (error.response?.status == 401 && g_loginHandler != null) {
            g_loginHandler.showLogin()
        }
        return Promise.reject(error);
    });
}

function getToken() {
    return window.localStorage.getItem(ID_TOKEN_KEY)
}

function saveToken(token) {
    window.localStorage.setItem(ID_TOKEN_KEY, token)
    g_access_token = token;
    createHttpConnection();
}

function destroyToken() {
    window.localStorage.removeItem(ID_TOKEN_KEY)
}

export function auth_checkUserAuthority(authorization) {
    if (!g_loginHandler) return true;

    return g_loginHandler.checkAuthority(authorization)
}

export class AuthApi {
    constructor(router, loginRedirectUrl) {
        const vm = this;
        g_loginHandler = this;
        vm.loginRedirectUrl = loginRedirectUrl
        vm.router = router;
        vm.user = {};

        createHttpConnection();

        router.beforeEach((to, from, next) => {
            console.log(to, from, next)
            const { authorization } = to.meta
            console.log(authorization)

            if (!vm.checkAuthority(authorization)) {
                console.log('!checkAuthority');
                if (vm.user.email == null) {
                    console.log('email is null');
                    alert("로그인이 필요합니다.")
                    return next({ path: loginRedirectUrl });
                }

                alert("사용 권한이 없습니다. " + authorization.toString() + " 권한이 필요합니다.")
                // 권한이 없는 유저는 not-found 페이지로 보낸다. ???

                // @yeony - main 페이지로 redirect
                return next({ path: '/main' });
            }

            // 22.09.27 - @yeony : admin 페이지 접근 차단

            if(to.path.includes('admin') && authorization.includes('ADMIN') && !vm.user.roles.includes('ADMIN')) {
                alert("사용 권한이 없습니다. ADMIN 권한이 필요합니다.")
                return next({path: '/main'})
            }

            next();
        });
    }

    checkAuthority(authorization) {
        if (!authorization || authorization.length == 0) return true;

        for (const role of authorization) {
            if (g_userInfo?.roles?.includes(role)) {
                return true;
            }
        }
        return false;
    }

    showLogin() {
        this.router.push(this.loginRedirectUrl);
    }

    setWebToken(jwt) {
        this.user = jwt;
    }

    async login(email, password) {
        let res = await unsecured_http.post("/auth/login", {
            email,
            password
        });
        saveToken(res.data.accessToken)
        return res.data;
    }

    async signUp(name, email, password) {
        let res = await unsecured_http.post("/auth/signup", {
            name,
            email,
            password
        });
        return res.data;
    }

    async getUserProfile() {
        let res = await http.get("/auth/user/me");
        g_userInfo = res.data;
    }

    logout() {
        destroyToken();
    }

    setAccessToken(token) {
        saveToken(token);
    }
}

//createHttpConnection();

