<template>
  <div class="account-pages my-5 pt-sm-5">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-10">
          <div class="text-center mb-5">
            <p class="font-size-24 text-muted mt-3">
              <b>로봇 작업계획 관리 시스템</b>
            </p>
          </div>
          <div class="card overflow-hidden">
            <div class="row g-0">
              <div class="col-lg-6">
                <div class="p-lg-5 p-4">
                  <div>
                    <h5>Welcome !</h5>
                    <p class="text-muted">Sign in to continue</p>
                  </div>
                  <b-alert :variant="notification.type" class="mt-3"
                           v-if="notification.message" :show="notificationAutoCloseDuration" dismissible @dismissed="clear">{{ notification.message }}</b-alert>
                  <b-alert variant="danger" class="mt-3"
                           v-model="isAuthError" :show="notificationAutoCloseDuration" dismissible @dismissed="clear">{{ authError }}</b-alert>
                  <div class="mt-4 pt-3">
                    <form @submit.prevent="login">
                      <div class="mb-3">
                        <label for="email" class="fw-semibold">Email</label>
                        <input type="text" class="form-control" v-model="credentials.userId" id="email" placeholder="Enter email" :class="{'is-invalid': submitted && $v.email.$error,}" />
                        <div v-if="submitted && $v.email.$error" class="invalid-feedback">
                          <span v-if="!$v.email.required" >Email is required.</span>
                          <span v-if="!$v.email.email">Please enter valid email.</span>
                        </div>
                      </div>

                      <div class="mb-3 mb-4">
                        <label for="userpassword" class="fw-semibold">Password</label>
                        <input type="password" v-model="credentials.password" class="form-control" id="userpassword" placeholder="Enter password"
                               :class="{'is-invalid': submitted && $v.password.$error,}"/>
                        <div v-if="submitted && !$v.password.required" class="invalid-feedback">
                          Password is required.
                        </div>
                      </div>

                      <div class="row align-items-center">
                        <div class="col-6">
                          <b-button variant="outline-primary" @click="googleLogin"><i class="bx bxl-google"></i> Google 계정 로그인</b-button>
                        </div>
                        <div class="col-6">
                          <div class="text-end">
                            <button class="btn btn-primary w-md waves-effect waves-light" type="submit">Log In</button>
                          </div>
                        </div>
                      </div>

                      <div class="mt-4">
                        <router-link to="/auth/recoverpwd-1" class="text-muted"><i class="mdi mdi-lock me-1"></i> Forgot your password?</router-link>
                      </div>
                    </form>
                  </div>
                </div>
              </div>

              <div class="col-lg-6">
                <div class="p-lg-5 p-4 bg-auth h-100 d-none d-lg-block">
                  <div class="bg-overlay"></div>
                </div>
              </div>
            </div>
          </div>
          <!-- end card -->
          <div class="mt-5 text-center">
            <p>
              Don't have an account ?
              <router-link
                  to="/register"
                  class="fw-semibold text-decoration-underline text-primary"
              >
                Sign up
              </router-link>
            </p>
          </div>
        </div>
        <!-- end col -->
      </div>
      <!-- end row -->
    </div>
    <!-- end container -->
  </div>
  <!-- end account page -->
</template>

<script>
import { required, email } from "vuelidate/lib/validators";

import {
  authMethods,
  authFackMethods,
  notificationMethods,
} from "@/state/helpers";

import appConfig from "@/app.config";

/**
 * Auth-login component
 */
export default {
  page: {
    title: "Login",
    meta: [
      {
        name: "description",
        content: appConfig.description,
      },
    ],
  },
  data() {
    return {
      credentials: {
        userId: '',
        password: ''
      },
      email: "admin@pichforest.com",
      password: "123456",
      submitted: false,
      authError: null,
      tryingToLogIn: false,
      isAuthError: false,
    };
  },
  computed: {
    notification() {
      return this.$store ? this.$store.state.notification : null;
    },
    notificationAutoCloseDuration() {
      return this.$store && this.$store.state.notification ? 5 : 0;
    },
  },
  validations: {
    email: { required, email },
    password: { required },
  },
  mounted() {
    const vm = this;
    vm.href = window.location.href.substr(0, window.location.href.lastIndexOf('/'));
    vm.hostName = vm.href + '/oauth2/authorize/google';
    vm.redirectUri = vm.href + '/oauth2/redirect';
    vm.googleApiUrl = vm.hostName + '?redirect_uri=' + vm.redirectUri;
  },
  methods: {
    ...authMethods,
    ...authFackMethods,
    ...notificationMethods,

    googleLogin() {
      const vm = this;
      window.location.href = vm.googleApiUrl;
    },

    async login() {
      const vm = this;
      this.$_app.login(vm.credentials.userId, vm.credentials.password)
          .then(res => {
            window.location.href = "/";
          })
          .catch(err => {
            alert(err);
          })
    }

  },
};
</script>