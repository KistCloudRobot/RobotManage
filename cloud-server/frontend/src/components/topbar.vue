<style lang="scss" scoped>
::v-deep {
  .ddd ul.dropdown-menu {
    display: block !important;
    margin-top: 50px!important;
  }
}
</style>
<script>
// import simplebar from "simplebar-vue";

import i18n from "../i18n";
import {useI18n} from "vue-i18n";
import BDropdown from "../mdk/BootstrapVue3/components/BDropdown/BDropdown.vue";

/**
 * Topbar component
 */
export default {
  components: {
    BDropdown
  },
  props: {
    type: {
      type: String,
      required: true,
    },
    layoutscroll: {
      type: Boolean,
      required: true,
    },
  },
  setup() {
    const { t } = useI18n();

    const initFullScreen = () => {
      document.body.classList.toggle("fullscreen-enable");
      if (
          !document.fullscreenElement &&
          /* alternative standard method */ !document.mozFullScreenElement &&
          !document.webkitFullscreenElement
      ) {
        // current working methods
        if (document.documentElement.requestFullscreen) {
          document.documentElement.requestFullscreen();
        } else if (document.documentElement.mozRequestFullScreen) {
          document.documentElement.mozRequestFullScreen();
        } else if (document.documentElement.webkitRequestFullscreen) {
          document.documentElement.webkitRequestFullscreen(
              Element.ALLOW_KEYBOARD_INPUT
          );
        }
      } else {
        if (document.cancelFullScreen) {
          document.cancelFullScreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        }
      }
    }

    return {
      t,
      initFullScreen,
    }
  },
  data() {
    return {
      username: this.$_app.user.email,
      lan: i18n.global.locale,
      text: null,
      flag: null,
      value: null,
      isDropdown: false,
      languages: [
        {
          flag: require("../assets/images/flags/us.jpg"),
          language: "en",
          title: "English",
        },
        {
          flag: require("../assets/images/flags/spain.jpg"),
          language: "es",
          title: "Spanish",
        },
        {
          flag: require("../assets/images/flags/germany.jpg"),
          language: "de",
          title: "German",
        },
        {
          flag: require("../assets/images/flags/italy.jpg"),
          language: "it",
          title: "Italian",
        },
        {
          flag: require("../assets/images/flags/russia.jpg"),
          language: "ru",
          title: "Russian",
        },
      ]
    }
  },
  methods: {
    logout() {
      const vm = this;
      vm.$_app.logout();
      // vm.$router.push( {path: '/login'} );
      vm.$router.go();
    },
    login() {
      const vm = this;

      vm.$router.push({path: '/login'})
    },
    test(d) {
      const vm = this;
      if(vm.isDropdown == true) {
        vm.isDropdown = false;
      } else if(vm.isDropdown == false) {
        vm.isDropdown = true;
      }
    },
    toggleMenu() {
      this.$parent.toggleMenu();
    },
    setLanguage(locale, country, flag) {
      this.lan = locale;
      this.text = country;
      this.flag = flag;
      i18n.locale = locale;
    },
    toggleRightSidebar() {
      this.$parent.toggleRightSidebar();
    },
  },
  mounted() {
    this.value = this.languages.find((x) => x.language === i18n.global.locale);

    if(!this.value) {
      this.value = this.languages[0]
    }
    console.log(this.value)
    this.text = this.value.title;
    this.flag = this.value.flag;

  },
  // components: { simplebar },
  watch: {
    type: {
      immediate: true,
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          switch (newVal) {
            case "dark":
              document.body.setAttribute("data-topbar", "dark");
              break;
            case "light":
              document.body.setAttribute("data-topbar", "light");
              document.body.removeAttribute("data-layout-size", "boxed");
              break;
            default:
              document.body.setAttribute("data-topbar", "dark");
              break;
          }
        }
      },
    },
    layoutscroll: {
      immediate: true,
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          switch (newVal) {
            case false:
              document.body.setAttribute("data-layout-scrollable", "false");
              break;
            case true:
              document.body.setAttribute("data-layout-scrollable", "true");
              break;
            default:
              document.body.setAttribute("data-layout-scrollable", "false");
              break;
          }
        }
      },
    },
  },
};
</script>

<template>
  <header id="page-topbar">
    <div class="navbar-header">
      <div class="d-flex">
        <!-- LOGO -->
        <div class="navbar-brand-box ps-3 pe-1">
          <router-link to="/" class="logo logo-dark">
            <span class="logo-sm">
              <img src="@/assets/images/logo-sm.png" alt="" height="22" />
            </span>
            <span class="logo-lg">
              <img src="@/assets/images/logo-dark.png" alt="" height="23" />
            </span>
          </router-link>

          <router-link to="/" class="logo logo-light">
            <span class="logo-sm">
<!--              <h4 style="margin-top: 1rem">클라우드 로봇</h4>-->
              <img src="@/assets/images/logo-sm-light.png" alt="" height="22" />
            </span>
            <span class="logo-lg">
              <h5 style="margin-top: 1rem" class="text-white">로봇 작업계획 관리 시스템</h5>
<!--              <img src="@/assets/images/logo-light.png" alt="" height="23" />-->
            </span>
          </router-link>
        </div>

        <button
          type="button"
          class="btn btn-sm px-3 font-size-16 vertinav-toggle header-item waves-effect"
          id="vertical-menu-btn"
          @click="toggleMenu()"
        >
          <i class="fa fa-fw fa-bars"></i>
        </button>

        <button
          type="button"
          class="btn btn-sm px-3 font-size-16 horinav-toggle header-item waves-effect waves-light"
          data-bs-toggle="collapse"
          data-bs-target="#topnav-menu-content"
          @click="toggleMenu()"
        >
          <i class="fa fa-fw fa-bars"></i>
        </button>

        <!-- App Search-->
<!--        <form class="app-search d-none d-lg-block">-->
<!--          <div class="position-relative">-->
<!--            <input-->
<!--              type="text"-->
<!--              class="form-control"-->
<!--              :placeholder="$t('navbar.search.text')"-->
<!--            />-->
<!--            <span class="mdi mdi-magnify"></span>-->
<!--          </div>-->
<!--        </form>-->
      </div>

      <div class="d-flex">
<!--        <b-dropdown-->
<!--          variant="white"-->
<!--          class="d-inline-block d-lg-none ms-2"-->
<!--          toggle-class="header-item noti-icon"-->
<!--          menu-class="dropdown-menu-lg dropdown-menu-end p-0"-->
<!--        >-->
<!--          <template #button-content>-->
<!--            <i class="mdi mdi-magnify"></i>-->
<!--          </template>-->
<!--          <form class="p-3">-->
<!--            <div class="form-group m-0">-->
<!--              <div class="input-group">-->
<!--                <input-->
<!--                  type="text"-->
<!--                  class="form-control"-->
<!--                  :placeholder="$t('navbar.search.text')"-->
<!--                  aria-label="Recipient's username"-->
<!--                />-->
<!--                <div class="input-group-append">-->
<!--                  <button class="btn btn-primary" type="submit">-->
<!--                    <i class="mdi mdi-magnify"></i>-->
<!--                  </button>-->
<!--                </div>-->
<!--              </div>-->
<!--            </div>-->
<!--          </form>-->
<!--        </b-dropdown>-->

<!--        <b-dropdown-->
<!--          class="d-inline-block"-->
<!--          variant="white"-->
<!--          right-->
<!--          toggle-class="header-item"-->
<!--          menu-class="dropdown-menu-end"-->
<!--        >-->
<!--          <template v-slot:button-content>-->
<!--            <img class :src="flag" alt="Header Language" height="16" />-->
<!--            {{ text }}-->
<!--          </template>-->
<!--          <b-dropdown-item-->
<!--            class="notify-item"-->
<!--            v-for="(entry, i) in languages"-->
<!--            :key="`Lang${i}`"-->
<!--            :value="entry"-->
<!--            @click="setLanguage(entry.language, entry.title, entry.flag)"-->
<!--            :class="{ active: lan === entry.language }"-->
<!--          >-->
<!--            <img-->
<!--              :src="`${entry.flag}`"-->
<!--              alt="user-image"-->
<!--              class="me-1"-->
<!--              height="12"-->
<!--            />-->
<!--            <span class="align-middle">{{ entry.title }}</span>-->
<!--          </b-dropdown-item>-->
<!--        </b-dropdown>-->

<!--        <div class="dropdown d-none d-lg-inline-block ms-1">-->
<!--          <button-->
<!--            type="button"-->
<!--            class="btn header-item noti-icon waves-effect"-->
<!--            @click="initFullScreen"-->
<!--          >-->
<!--            <i class="mdi mdi-fullscreen"></i>-->
<!--          </button>-->
<!--        </div>-->

<!--        <b-dropdown-->
<!--          variant="black"-->
<!--          class="d-inline-block"-->
<!--          toggle-class="header-item noti-icon"-->
<!--          menu-class="dropdown-menu-lg dropdown-menu-end p-0"-->
<!--        >-->
<!--          <template #button-content>-->
<!--            <i class="mdi mdi-bell"></i>-->
<!--            <span class="badge bg-danger rounded-pill">3</span>-->
<!--          </template>-->
<!--          <div class="p-3">-->
<!--            <div class="row align-items-center">-->
<!--              <div class="col">-->
<!--                <h6 class="m-0">-->
<!--                  {{ $t("navbar.dropdown.notification.text") }}-->
<!--                </h6>-->
<!--              </div>-->
<!--              <div class="col-auto">-->
<!--                <a href="javascript:void(0);" class="small" key="t-view-all">-->
<!--                  {{ $t("navbar.dropdown.notification.subtext") }}</a-->
<!--                >-->
<!--              </div>-->
<!--            </div>-->
<!--          </div>-->
<!--          <div style="max-height: 230px">-->
<!--            <a href="javascript:void(0);" class="text-reset notification-item d-block active">-->
<!--              <div class="d-flex">-->
<!--                <div class="avatar-xs me-3">-->
<!--                  <span-->
<!--                    class="avatar-title bg-primary rounded-circle font-size-16"-->
<!--                  >-->
<!--                    <i class="bx bx-cart"></i>-->
<!--                  </span>-->
<!--                </div>-->
<!--                <div class="flex-1">-->
<!--                  <h6 class="mt-0 mb-1" key="t-your-order">-->
<!--                    {{ $t("navbar.dropdown.notification.order.title") }}-->
<!--                  </h6>-->
<!--                  <div class="font-size-13 text-muted">-->
<!--                    <p class="mb-1" key="t-grammer">-->
<!--                      {{ $t("navbar.dropdown.notification.order.text") }}-->
<!--                    </p>-->
<!--                    <p class="mb-0 font-size-12">-->
<!--                      <i class="mdi mdi-clock-outline"></i>-->
<!--                      <span key="t-min-ago">{{-->
<!--                        $t("navbar.dropdown.notification.order.time")-->
<!--                      }}</span>-->
<!--                    </p>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </a>-->
<!--            <a href="javascript:void(0);" class="text-reset notification-item d-block">-->
<!--              <div class="d-flex">-->
<!--                <img-->
<!--                  src="@/assets/images/users/avatar-3.jpg"-->
<!--                  class="me-3 rounded-circle avatar-xs"-->
<!--                  alt="user-pic"-->
<!--                />-->
<!--                <div class="flex-1">-->
<!--                  <h6 class="mt-0 mb-1">-->
<!--                    {{ $t("navbar.dropdown.notification.james.title") }}-->
<!--                  </h6>-->
<!--                  <div class="font-size-13 text-muted">-->
<!--                    <p class="mb-1" key="t-simplified">-->
<!--                      {{ $t("navbar.dropdown.notification.james.text") }}-->
<!--                    </p>-->
<!--                    <p class="mb-0 font-size-12">-->
<!--                      <i class="mdi mdi-clock-outline"></i>-->
<!--                      <span key="t-hours-ago">{{-->
<!--                        $t("navbar.dropdown.notification.james.time")-->
<!--                      }}</span>-->
<!--                    </p>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </a>-->
<!--            <a href="javascript:void(0);" class="text-reset notification-item d-block">-->
<!--              <div class="d-flex">-->
<!--                <div class="avatar-xs me-3">-->
<!--                  <span-->
<!--                    class="avatar-title bg-success rounded-circle font-size-16"-->
<!--                  >-->
<!--                    <i class="bx bx-badge-check"></i>-->
<!--                  </span>-->
<!--                </div>-->
<!--                <div class="flex-1">-->
<!--                  <h6 class="mt-0 mb-1" key="t-shipped">-->
<!--                    {{ $t("navbar.dropdown.notification.item.title") }}-->
<!--                  </h6>-->
<!--                  <div class="font-size-13 text-muted">-->
<!--                    <p class="mb-1" key="t-grammer">-->
<!--                      {{ $t("navbar.dropdown.notification.item.text") }}-->
<!--                    </p>-->
<!--                    <p class="mb-0 font-size-12">-->
<!--                      <i class="mdi mdi-clock-outline"></i>-->
<!--                      <span key="t-min-ago">{{-->
<!--                        $t("navbar.dropdown.notification.item.time")-->
<!--                      }}</span>-->
<!--                    </p>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </a>-->
<!--            <a href="javascript:void(0);" class="text-reset notification-item d-block">-->
<!--              <div class="d-flex">-->
<!--                <img-->
<!--                  src="@/assets/images/users/avatar-4.jpg"-->
<!--                  class="me-3 rounded-circle avatar-xs"-->
<!--                  alt="user-pic"-->
<!--                />-->
<!--                <div class="flex-1">-->
<!--                  <h6 class="mt-0 mb-1">-->
<!--                    {{ $t("navbar.dropdown.notification.salena.title") }}-->
<!--                  </h6>-->
<!--                  <div class="font-size-13 text-muted">-->
<!--                    <p class="mb-1" key="t-occidental">-->
<!--                      {{ $t("navbar.dropdown.notification.salena.text") }}-->
<!--                    </p>-->
<!--                    <p class="mb-0 font-size-12">-->
<!--                      <i class="mdi mdi-clock-outline"></i>-->
<!--                      <span key="t-hours-ago">{{-->
<!--                        $t("navbar.dropdown.notification.salena.time")-->
<!--                      }}</span>-->
<!--                    </p>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </a>-->
<!--          </div>-->
<!--          <div class="p-2 border-top d-grid">-->
<!--            <a-->
<!--              class="btn btn-sm btn-link font-size-14 text-center"-->
<!--              href="javascript:void(0)"-->
<!--            >-->
<!--              <i class="mdi mdi-arrow-right-circle me-1"></i>-->
<!--              <span>{{ $t("navbar.dropdown.notification.button") }}</span>-->
<!--            </a>-->
<!--          </div>-->
<!--        </b-dropdown>-->

        <b-dropdown
          class="d-inline-block"
          :class="isDropdown? 'ddd' : ''"
          menu-class="dropdown-menu-end"
          variant="white"
          toggle-class="header-item"
          @click="test"
        >
          <template #button-content>
            <img
              class="rounded-circle header-profile-user"
              src="@/assets/images/users/avatar-1.jpg"
              alt="Header Avatar"
            />
            <span class="d-none d-xl-inline-block ms-1">{{
                username
            }}</span>
            <i class="mdi mdi-chevron-down d-none d-xl-inline-block"></i>
          </template>

          <!-- item-->
<!--          <h6 class="dropdown-header">-->
<!--            {{ $t("navbar.dropdown.profile.list.welcome") }}-->
<!--          </h6>-->
<!--          <b-dropdown-item-->
<!--            ><i-->
<!--              class="mdi mdi-account-circle text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-profile">{{-->
<!--              $t("navbar.dropdown.profile.list.profile")-->
<!--            }}</span></b-dropdown-item-->
<!--          >-->
<!--          <b-dropdown-item-->
<!--            ><i-->
<!--              class="mdi mdi-message-text-outline text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-messages">{{-->
<!--              $t("navbar.dropdown.profile.list.messages")-->
<!--            }}</span></b-dropdown-item-->
<!--          >-->
<!--          <b-dropdown-item-->
<!--            ><i-->
<!--              class="mdi mdi-calendar-check-outline text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-taskboard">{{-->
<!--              $t("navbar.dropdown.profile.list.taskboard")-->
<!--            }}</span></b-dropdown-item-->
<!--          >-->
<!--          <b-dropdown-item-->
<!--            ><i-->
<!--              class="mdi mdi-lifebuoy text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-help">{{-->
<!--              $t("navbar.dropdown.profile.list.help")-->
<!--            }}</span></b-dropdown-item-->
<!--          >-->
<!--          <div class="dropdown-divider"></div>-->
<!--          <b-dropdown-item-->
<!--            ><i-->
<!--              class="mdi mdi-wallet text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-balance"-->
<!--              >{{ $t("navbar.dropdown.profile.list.balance.text") }} :-->
<!--              <b>{{-->
<!--                $t("navbar.dropdown.profile.list.balance.amount")-->
<!--              }}</b></span-->
<!--            ></b-dropdown-item-->
<!--          >-->
<!--          <b-dropdown-item-->
<!--            ><span-->
<!--              class="badge bg-success bg-soft text-success mt-1 float-end"-->
<!--              >{{ $t("navbar.dropdown.profile.list.settings.badge") }}</span-->
<!--            ><i-->
<!--              class="mdi mdi-cog-outline text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-settings">{{-->
<!--              $t("navbar.dropdown.profile.list.settings.text")-->
<!--            }}</span></b-dropdown-item-->
<!--          >-->
<!--          <b-dropdown-item-->
<!--            ><i-->
<!--              class="mdi mdi-lock text-muted font-size-16 align-middle me-1"-->
<!--            ></i>-->
<!--            <span class="align-middle" key="t-lock-screen">{{-->
<!--              $t("navbar.dropdown.profile.list.lockscreen")-->
<!--            }}</span></b-dropdown-item-->
<!--          >-->

          <a href="javascript:;" v-on:click="logout" class="dropdown-item" v-if="$_app.user.email">
            <i class="mdi mdi-logout text-muted font-size-16 align-middle me-1" />
            <span class="align-middle" key="t-logout">{{ $t("navbar.dropdown.profile.list.logout") }}</span>
          </a>
          <a href="javascript:;" v-on:click="login" class="dropdown-item" v-else>
            <i class="mdi mdi-logout text-muted font-size-16 align-middle me-1" />
            <span class="align-middle" key="t-logout">{{ $t("navbar.dropdown.profile.list.login") }}</span>
          </a>
        </b-dropdown>

<!--        <div class="dropdown d-inline-block">-->
<!--          <button-->
<!--            type="button"-->
<!--            class="btn header-item noti-icon right-bar-toggle toggle-right"-->
<!--            @click="toggleRightSidebar"-->
<!--          >-->
<!--            <i class="bx bx-cog bx-spin toggle-right"></i>-->
<!--          </button>-->
<!--        </div>-->
      </div>
    </div>
  </header>
</template>
