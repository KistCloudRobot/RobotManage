<script>
// import { layoutMethods } from "@/state/helpers";
import {useStore} from "vuex";
/**
 * Right-bar component
 */
export default {
  setup() {
    const store = useStore();

    console.log(store)

    const layout = store ? store.state.layout.layoutType : {} || {}
    const mode = store ? store.state.layout.layoutMode : {} || {};
    const width = store ? store.state.layout.layoutWidth : {} || {};
    const topbar = store ? store.state.layout.topbar : {} || {};
    const scrollable = store
        ? store.state.layout.layoutScrollable
        : {} || {};
    const sidebarType = store
        ? store.state.layout.leftSidebarType
        : {} || {};
    const color = store ? store.state.layout.sidebarColor : {} || {};
    const loader = store ? store.state.layout.loader : {} || {};

    // const {...layoutMethods};

    const hide = () => {
      this.$parent.toggleRightSidebar();
    };
    const handleRightBarClick = (e, el) => {
      this.$parent.hideRightSidebar();
    };

    const middleware = (event, el) => {
      return !event.target.classList.contains("toggle-right");
    };

    /**
     * Change layout
     */
    const changeLayout = (layout) => {
      this.changeLayoutType({
        layoutType: layout,
      });
    };

    /**
     * Change layout mode
     */
    const layoutMode = (mode) => {
      this.changeLayoutMode({
        layoutMode: mode,
      });
      if (mode == "dark") {
        this.changeTopbar({
          topbar: "dark",
        });
      }
    };

    /**
     * Change layout width
     */
    const changeWidth = (width) => {
      return this.changeLayoutWidth({
        layoutWidth: width,
      });
    };


    /**
     * Change type
     */
    const changeType = (type) => {
      return this.changeLeftSidebarType({
        leftSidebarType: type,
      });
    }

    /**
     * Change sidebar color change
     */
    const sidebarColorChange = (color) => {
      return this.changeSidebarColor({
        sidebarColor: color,
      });
    };

    /**
     * Change topbar type
     */
    const changeTopbartype = (value) => {
      return this.changeTopbar({
        topbar: value,
      });
    };

    /**
     * Change layout position
     */
    const changePosition = (value) => {
      return this.changeLayoutPosition({
        layoutScrollable: value,
      });
    };

    /**
     * Change loader
     */
    const changeloader = () => {
      return this.changeLoaderValue({
        loader: this.loader,
      });
    };

    const config = {
      // handler: this.handleRightBarClick,
      // middleware: this.middleware,
      events: ["click"]
    };



    return {
      layout, mode, width, topbar, scrollable, sidebarType, color, loader, hide, handleRightBarClick, config, middleware,
      changeLayout, layoutMode, changeWidth, changeType, sidebarColorChange, changeTopbartype, changePosition, changeloader
    };
  },
  watch: {
    mode: {
      handler(value) {
        console.log(value)
        if (value === "dark") {
          document.body.setAttribute("data-layout-mode", "dark");
          document.body.setAttribute("data-topbar", "dark");
        } else {
          document.body.removeAttribute("data-layout-mode");
          document.body.removeAttribute("data-topbar");
        }
      },
    },
  },
};
</script>

<template>
  <div>
    <!-- Right Sidebar -->
    <div class="right-bar" v-click-outside="config">
      <div data-simplebar class="h-100">
        <div class="rightbar-title d-flex align-items-center bg-dark p-3">
          <h5 class="m-0 me-2 text-white">Theme Customizer</h5>
          <a
            href="javascript:void(0);"
            class="right-bar-toggle ms-auto"
            @click="hide"
          >
            <i class="mdi mdi-close noti-icon"></i>
          </a>
        </div>

        <!-- Settings -->
        <hr class="m-0" />

        <div class="p-4">
          <h6 class="mb-3">Layout</h6>
          <b-form-radio-group
            class="form-check-inline"
            v-model="layout"
            stacked
            @input="changeLayout($event)"
            id="isLayout"
          >
            <b-form-radio value="vertical" class="mb-1">Vertical</b-form-radio>
            <b-form-radio value="horizontal" class="mb-1"
              >Horizontal</b-form-radio
            >
          </b-form-radio-group>

          <h6 class="mt-4 mb-3">Layout Mode</h6>

          <b-form-radio-group
            class="form-check-inline"
            v-model="mode"
            stacked
            @input="layoutMode($event)"
            id="layoutmode"
          >
            <b-form-radio value="light" class="mb-1">Light</b-form-radio>
            <b-form-radio value="dark" class="mb-1">Dark</b-form-radio>
          </b-form-radio-group>

          <h6 class="mt-4 mb-3">Layout Width</h6>

          <b-form-radio-group
            v-model="width"
            stacked
            @input="changeWidth($event)"
          >
            <b-form-radio value="fluid" class="mb-1">Fluid</b-form-radio>
            <b-form-radio value="boxed" class="mb-1">Boxed</b-form-radio>
          </b-form-radio-group>

          <h6 class="mt-4 mb-3">Layout Position</h6>

          <b-form-radio-group
            v-model="scrollable"
            stacked
            @input="changePosition($event)"
          >
            <b-form-radio :value="false" class="mb-1">Fixed</b-form-radio>
            <b-form-radio :value="true" class="mb-1">Scrollable</b-form-radio>
          </b-form-radio-group>

          <h6 class="mt-4 mb-3">Topbar Color</h6>

          <b-form-radio-group
            v-model="topbar"
            stacked
            @input="changeTopbartype($event)"
          >
            <b-form-radio value="dark" class="mb-1">Dark</b-form-radio>
            <b-form-radio value="light" class="mb-1">Light</b-form-radio>
          </b-form-radio-group>
          <div v-if="layout === 'vertical'">
            <h6 class="mt-4 mb-3 sidebar-setting">Sidebar Size</h6>

            <b-form-radio-group
              v-model="sidebarType"
              stacked
              @input="changeType($event)"
            >
              <b-form-radio value="default" class="mb-1">Default</b-form-radio>
              <b-form-radio value="compact" class="mb-1">Compact</b-form-radio>
              <b-form-radio value="icon" class="mb-1"
                >Icon (Small View)</b-form-radio
              >
            </b-form-radio-group>

            <h6 class="mt-4 mb-3 sidebar-setting">Sidebar Color</h6>
            <b-form-radio-group
              v-model="color"
              stacked
              @input="sidebarColorChange($event)"
            >
              <b-form-radio value="light" class="mb-1">Light</b-form-radio>
              <b-form-radio value="dark" class="mb-1">Dark</b-form-radio>
              <b-form-radio value="brand" class="mb-1">Brand</b-form-radio>
            </b-form-radio-group>
          </div>
          <h6 class="mt-4 mb-3 sidebar-setting">Loader</h6>

          <b-form-checkbox
            v-model="loader"
            name="check-button"
            @input="changeloader"
            >Preloader</b-form-checkbox
          >
        </div>
      </div>
      <!-- end slimscroll-menu-->
    </div>
    <!-- /Right-bar -->

    <!-- Right bar overlay-->
    <div class="rightbar-overlay"></div>
  </div>
</template>
