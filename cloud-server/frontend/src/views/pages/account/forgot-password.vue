<script>
import { authMethods } from "@/state/helpers";
import { required, email } from "vuelidate/lib/validators";
import appConfig from "@/app.config";

/**
 * Forgot-password component
 */
export default {
  page: {
    title: "Forgot-password",
    meta: [
      {
        name: "description",
        content: appConfig.description,
      },
    ],
  },
  data() {
    return {
      email: "",
      submitted: false,
      error: null,
      tryingToReset: false,
      isResetError: false,
    };
  },
  validations: {
    email: { required, email },
  },
  methods: {
    ...authMethods,
    // Try to register the user in with the email, fullname
    // and password they provided.
    tryToReset() {
      this.submitted = true;
      // stop here if form is invalid
      this.$v.$touch();

      if (this.$v.$invalid) {
        return;
      } else {
        if (process.env.VUE_APP_DEFAULT_AUTH === "firebase") {
          this.tryingToReset = true;
          // Reset the authError if it existed.
          this.error = null;
          return (
            this.resetPassword({
              email: this.email,
            })
              // eslint-disable-next-line no-unused-vars
              .then((token) => {
                this.tryingToReset = false;
                this.isResetError = false;
              })
              .catch((error) => {
                this.tryingToReset = false;
                this.error = error ? error : "";
                this.isResetError = true;
              })
          );
        }
      }
    },
  },
};
</script>

<template>
  <div class="account-pages my-5 pt-sm-5">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-10">
          <div class="text-center mb-5">
            <router-link to="/" class="auth-logo">
              <img
                src="@/assets/images/logo-dark.png"
                alt=""
                height="28"
                class="auth-logo-dark"
              />
              <img
                src="@/assets/images/logo-light.png"
                alt=""
                height="28"
                class="auth-logo-light"
              />
            </router-link>
            <p class="font-size-15 text-muted mt-3">
              Responsive <b>Bootstrap 5</b> Admin Dashboard
            </p>
          </div>
          <div class="card overflow-hidden">
            <div class="row g-0">
              <div class="col-lg-6">
                <div class="p-lg-5 p-4">
                  <div>
                    <h5>Reset Password</h5>
                    <p class="text-muted">Re-Password with Samply.</p>
                  </div>

                  <div class="mt-4 pt-3">
                    <div class="alert alert-success mb-4" role="alert">
                      Enter your Email and instructions will be sent to you!
                    </div>
                    <b-alert
                      v-model="isResetError"
                      class="mb-4"
                      variant="danger"
                      dismissible
                      >{{ error }}</b-alert
                    >
                    <form @submit.prevent="tryToReset">
                      <div class="mb-3">
                        <label class="fw-semibold" for="useremail">Email</label>
                        <input
                          type="email"
                          v-model="email"
                          class="form-control"
                          id="useremail"
                          placeholder="Enter email"
                          :class="{
                            'is-invalid': submitted && $v.email.$error,
                          }"
                        />
                        <div
                          v-if="submitted && $v.email.$error"
                          class="invalid-feedback"
                        >
                          <span v-if="!$v.email.required"
                            >Email is required.</span
                          >
                          <span v-if="!$v.email.email"
                            >Please enter valid email.</span
                          >
                        </div>
                      </div>

                      <div class="mt-4 text-end">
                        <button
                          class="btn btn-primary w-md waves-effect waves-light"
                          type="submit"
                        >
                          Reset
                        </button>
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
              Remember It ?
              <router-link
                to="/auth/login-1"
                class="fw-semibold text-decoration-underline"
              >
                Login
              </router-link>
            </p>
            <p>
              © {{ new Date().getFullYear() }} <b>Samply</b>. Crafted with
              <i class="mdi mdi-heart text-danger"></i> by Pichforest
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
