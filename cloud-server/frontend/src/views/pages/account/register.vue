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
                    <h5>Register account</h5>
                    <p class="text-muted">Get your free Samply account now.</p>
                  </div>

                  <div class="mt-4 pt-3">
                    <b-alert
                        v-model="registerSuccess"
                        class="mt-3"
                        variant="success"
                        dismissible
                    >Registration successfull.</b-alert
                    >

                    <b-alert
                        v-model="isRegisterError"
                        class="mt-3"
                        variant="danger"
                        dismissible
                        :show="notificationAutoCloseDuration"
                        @dismissed="clear"
                    >{{ regError }}</b-alert
                    >

                    <div
                        v-if="notification.message"
                        :class="'alert alert-' + notification.type"
                    >
                      {{ notification.message }}
                    </div>

                    <form @submit.prevent="signUp">
                      <div class="mb-3">
                        <label class="fw-semibold" for="username"
                        >Username</label
                        >
                        <input
                            v-model="credentials.name"
                            type="text"
                            class="form-control"
                            id="username"
                            :class="{
                            'is-invalid': submitted && $v.credentials.username.$error,
                          }"
                            placeholder="Enter username"
                        />
                        <div
                            v-if="submitted && !$v.user.username.required"
                            class="invalid-feedback"
                        >
                          Username is required.
                        </div>
                      </div>
                      <div class="mb-3">
                        <label class="fw-semibold" for="useremail">Email</label>
                        <input
                            v-model="credentials.userId"
                            type="email"
                            class="form-control"
                            id="useremail"
                            placeholder="Enter email"
                            :class="{
                            'is-invalid': submitted && $v.credentials.userId.$error,
                          }"
                        />
                        <div
                            v-if="submitted && $v.credentials.userId.$error"
                            class="invalid-feedback"
                        >
                          <span v-if="!$v.credentials.userId.required"
                          >Email is required.</span
                          >
                          <span v-if="!$v.credentials.userId.email"
                          >Please enter valid email.</span
                          >
                        </div>
                      </div>
                      <div class="mb-3">
                        <label class="fw-semibold" for="userpassword"
                        >Password</label
                        >
                        <input
                            v-model="credentials.password"
                            type="password"
                            class="form-control"
                            id="userpassword"
                            placeholder="Enter password"
                            :class="{
                            'is-invalid': submitted && $v.credentials.password.$error,
                          }"
                        />
                        <div
                            v-if="submitted && !$v.credentials.password.required"
                            class="invalid-feedback"
                        >
                          Password is required.
                        </div>
                      </div>

                      <div class="mt-4 text-end">
                        <button
                            class="btn btn-primary w-md waves-effect waves-light"
                            type="submit"
                        >
                          Register
                        </button>
                      </div>

<!--                      <div class="mt-4 text-center">-->
<!--                        <p class="mb-0 text-muted">-->
<!--                          By registering you agree to the Samply-->
<!--                          <a-->
<!--                              href="javascript:void(0);"-->
<!--                              class="text-primary fw-semibold text-decoration-underline"-->
<!--                          >Terms of Use</a-->
<!--                          >-->
<!--                        </p>-->
<!--                      </div>-->
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
              Already have an account ?
              <router-link
                  to="/auth/login-1"
                  class="fw-semibold text-decoration-underline"
              >
                Login
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
import { mapState } from "vuex";
import appConfig from "@/app.config";

/**
 * Auth-Register component
 */
export default {
  page: {
    title: "Register",
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
        password: '',
        name: ''
      },
      user: { username: "", email: "", password: "" },
      submitted: false,
      regError: null,
      tryingToRegister: false,
      isRegisterError: false,
      registerSuccess: false,
    };
  },
  computed: {
    ...mapState("authfack", ["status"]),
    notification() {
      return this.$store ? this.$store.state.notification : null;
    },
    notificationAutoCloseDuration() {
      return this.$store && this.$store.state.notification ? 5 : 0;
    },
  },
  validations: {
    user: {
      username: { required },
      email: { required, email },
      password: { required },
    },
  },
  methods: {
    ...authMethods,
    ...authFackMethods,
    ...notificationMethods,
    async signUp() {
      const vm = this;
      this.$_app.signUp(vm.credentials.name, vm.credentials.userId, vm.credentials.password)
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
