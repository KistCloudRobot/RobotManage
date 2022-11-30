<template>
  <div class="login-form">
    <div class="app-top-box"
         style="box-sizing: border-box; color: rgba(0, 0, 0, 0.8); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-variant-ligatures: normal; orphans: 2; text-decoration-thickness: initial; widows: 2;">
      <header class="app-header" style="box-sizing: border-box; height: 60px; position: relative; z-index: 10;">
        <div class="container"
             style="box-sizing: border-box; margin-left: auto; margin-right: auto; max-width: 1300px; padding-left: 30px; padding-right: 30px; width: 1300px;">
          <div class="app-branding" style="box-sizing: border-box; float: left;">
            <a class="app-title"
               href="http://localhost:8080/"
               style="box-sizing: border-box; color: #2098f3; cursor: pointer; font-size: 1.35em; line-height: 60px; overflow-wrap: break-word; text-decoration: none; vertical-align: middle;">연료 전지</a>
          </div>
          <div class="app-options" style="box-sizing: border-box; float: right;">
            <nav class="app-nav" style="box-sizing: border-box;">
              <ul style="box-sizing: border-box; margin: 0px; padding: 0px;">
                <li style="box-sizing: border-box; display: inline-block; list-style-type: none;">
                  <a aria-current="page"
                     class="active"
                     href="http://localhost:8080/login"
                     style="box-sizing: border-box; color: #0f96f6; cursor: pointer; display: inline-block; line-height: 60px; overflow-wrap: break-word; padding-left: 15px; padding-right: 15px; text-decoration: none; vertical-align: middle;">Login</a>
                </li>
                <li style="box-sizing: border-box; display: inline-block; list-style-type: none;">
                  <a
                      href="http://localhost:8080/signup"
                      style="box-sizing: border-box; color: rgba(0, 0, 0, 0.65); cursor: pointer; display: inline-block; line-height: 60px; overflow-wrap: break-word; padding-left: 15px; padding-right: 15px; text-decoration: none; vertical-align: middle;">Signup</a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </header>
    </div>
    <div class="app-body"
         style="box-sizing: border-box; color: rgba(0, 0, 0, 0.8); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px; font-variant-ligatures: normal; orphans: 2; text-decoration-thickness: initial; widows: 2;">
      <div class="login-container" style="box-sizing: border-box; text-align: center;">
        <div class="login-content"
             style="background-color: white; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; border-top-left-radius: 2px; border-top-right-radius: 2px; box-shadow: rgba(0, 0, 0, 0.27) 0px 1px 11px; box-sizing: border-box; display: inline-block; margin-top: 30px; padding: 35px; position: relative; vertical-align: middle; width: 500px;">
          <h1 class="login-title"
              style="box-sizing: border-box; color: rgba(0, 0, 0, 0.65); font-size: 1.5em; font-weight: 500; margin-bottom: 30px; margin-top: 0px;">
            연료 전지 O&M</h1>
          <div class="social-login" style="box-sizing: border-box;">
            <!-- SecurityConfig.configure(HttpSecurity http) 참조-->
            <!-- @zee 2022.04.25 MDK-OAuth. 접속 주소 변경 시 redirect_uri 변경 필요 -->
            <a v-if="true" class="btn btn-block social-btn google"
               href="http://localhost:6090/oauth2/authorize/google?redirect_uri=http://localhost:8080/oauth2/redirect"
               style="background-image: none; border-bottom-left-radius: 4px; border-bottom-right-radius: 4px; border-top-left-radius: 4px; border-top-right-radius: 4px; border: 1px solid rgb(232, 232, 232); box-sizing: border-box; color: rgba(0, 0, 0, 0.65); cursor: pointer; display: block; height: 45px; line-height: 45px; margin-bottom: 15px; margin-top: 7px; outline: none; overflow-wrap: break-word; padding: 0px 20px; position: relative; text-decoration: none; touch-action: manipulation; transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1) 0s; white-space: nowrap; width: 430px;"><img
                alt="Google" src="http://localhost:8080/static/media/google-logo.36158af3.png"
                style="box-sizing: border-box; float: left; height: 32px; margin-top: 10px;"/>Log in with Google</a>
            <a v-if="false"
               class="btn btn-block social-btn facebook"
                href="http://localhost:6090/oauth2/authorize/facebook?redirect_uri=http://localhost:8080/oauth2/redirect"
                style="background-image: none; border-bottom-left-radius: 4px; border-bottom-right-radius: 4px; border-top-left-radius: 4px; border-top-right-radius: 4px; border: 1px solid rgb(232, 232, 232); box-sizing: border-box; color: rgba(0, 0, 0, 0.65); cursor: pointer; display: block; height: 45px; line-height: 45px; margin-bottom: 15px; outline: none; overflow-wrap: break-word; padding: 0px 20px; position: relative; text-decoration: none; touch-action: manipulation; transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1) 0s; white-space: nowrap; width: 430px;"><img
                alt="Facebook"
                src="data:image/png;base64,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"
                style="box-sizing: border-box; float: left; height: 24px; margin-left: 3px; margin-top: 10px;"/>Log in
              with
              Facebook</a>
            <a v-if="false"
               class="btn btn-block social-btn github"
               href="http://localhost:6090/oauth2/authorize/github?redirect_uri=http://localhost:8080/oauth2/redirect"
               style="background-image: none; border-bottom-left-radius: 4px; border-bottom-right-radius: 4px; border-top-left-radius: 4px; border-top-right-radius: 4px; border: 1px solid rgb(232, 232, 232); box-sizing: border-box; color: rgba(0, 0, 0, 0.65); cursor: pointer; display: block; height: 45px; line-height: 45px; margin-bottom: 15px; outline: none; overflow-wrap: break-word; padding: 0px 20px; position: relative; text-decoration: none; touch-action: manipulation; transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1) 0s; white-space: nowrap; width: 430px;"><img
                alt="Github"
                src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAYAAAA5ZDbSAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpFNTE3OEEzMjk5QTAxMUUyOUExNUJDMTA0NkE4OTA0RCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDoyQTQxNEFCQzk5QTExMUUyOUExNUJDMTA0NkE4OTA0RCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkU1MTc4QTMwOTlBMDExRTI5QTE1QkMxMDQ2QTg5MDREIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkU1MTc4QTMxOTlBMDExRTI5QTE1QkMxMDQ2QTg5MDREIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+R7ClIwAADR5JREFUeNrsnQuw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"
                style="box-sizing: border-box; float: left; height: 24px; margin-left: 3px; margin-top: 10px;"/>Log in
              with
              Github</a>
          </div>
          <div class="or-separator"
               style="border-bottom-color: rgb(238, 238, 238); border-bottom-style: solid; border-bottom-width: 1px; box-sizing: border-box; font-size: 1em; margin-bottom: 30px; margin-top: 20px; padding: 10px 0px; position: relative;">
            <span class="or-text" color="rgba(0, 0, 0, 0.45)"
                  style="box-sizing: border-box; left: 197.797px; padding: 10px; position: absolute; top: 0px;">OR</span>
          </div>
          <form @submit.prevent="login()" style="box-sizing: border-box;">
            <div class="form-item" style="box-sizing: border-box; margin-bottom: 18px;">
              <input class="form-control"
                     name="email" required=""
                     placeholder="이메일"
                     v-model="credentials.userId"
                     style="background-color: white; background-image: none; border-bottom-left-radius: 4px; border-bottom-right-radius: 4px; border-top-left-radius: 4px; border-top-right-radius: 4px; border: 1px solid rgb(232, 232, 232); box-shadow: rgba(0, 0, 0, 0.075) 0px 1px 1px inset; color: rgba(0, 0, 0, 0.8); font-size: 0.87em; height: 45px; line-height: 45px; list-style: none; margin: 0px; padding: 4px 11px; position: relative; transition: all 0.3s ease 0s; width: 430px;"
                     type="email" value=""/>
            </div>
            <div class="form-item" style="box-sizing: border-box; margin-bottom: 18px;">
              <input class="form-control"
                     name="password" required=""
                     placeholder="암호"
                     style="background-color: white; background-image: none; border-bottom-left-radius: 4px; border-bottom-right-radius: 4px; border-top-left-radius: 4px; border-top-right-radius: 4px; border: 1px solid rgb(232, 232, 232); box-shadow: rgba(0, 0, 0, 0.075) 0px 1px 1px inset; color: rgba(0, 0, 0, 0.8); font-size: 0.87em; height: 45px; line-height: 45px; list-style: none; margin: 0px; padding: 4px 11px; position: relative; transition: all 0.3s ease 0s; width: 430px;"
                     v-model="credentials.password"
                     type="password" value=""/>
            </div>
            <div class="form-item" style="box-sizing: border-box; margin-bottom: 18px;">
              <b-button class="btn btn-block btn-primary"
                      style="background-color: #2098f3; background-image: none; border-bottom-left-radius: 4px; border-bottom-right-radius: 4px; border-top-left-radius: 4px; border-top-right-radius: 4px; border: 1px solid rgb(32, 152, 243); color: white; cursor: pointer; display: block; font-size: 14px; height: 45px; line-height: 45px; outline: none; padding: 0px 20px; position: relative; touch-action: manipulation; transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1) 0s; white-space: nowrap; width: 430px;"
                      type="submit">Login
              </b-button>
            </div>
          </form>
          <span class="signup-link" color="rgba(0, 0, 0, 0.65)" style="box-sizing: border-box; font-size: 14px;">New user?&nbsp;<a
              href="/signup"
              style="box-sizing: border-box; color: #2098f3; cursor: pointer; overflow-wrap: break-word; text-decoration: none;">Sign up!</a></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppOptions from '@/config/AppOptions'
export default {
  name: 'login',

  data() {
    return {
      loginSuccess: false,
      loginError: false,
      status: null,
      error: false,
      credentials: {
        userId: '',
        password: ''
      },
      appOptions: {...AppOptions, appEmpty: true},
    }
  },
  methods: {
    async login() {
      const vm = this;
      this.$_app.login(vm.credentials.userId, vm.credentials.password)
      .then(res => {
        // 주소 이동( @TODO 리다이렉트 주소 변경 필요)
        // await vm.$router.push({path: '/dashboard' })
        window.location.href = "/";
      })
      .catch(err => {
        alert(err);
      })
    }
  },
  mounted() {
    const vm = this;
    vm.$parent.appOptions.appEmpty = true;
  },
}
</script>
