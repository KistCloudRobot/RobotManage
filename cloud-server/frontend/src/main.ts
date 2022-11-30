import { createApp, reactive } from "vue";
import App from "./App.vue";
import router from "./router";
import {store} from '@/state/store';
// import BootstrapVue from "bootstrap-vue";
import {BootstrapVue3} from "bootstrap-vue-3";
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css'
import "bootstrap"
import i18n from "@/i18n";
import {Application} from "./global";
import MDKTable from "@/mdk/MDKTable2.vue";
import MDKForm from "@/mdk/MDKForm.vue";
import MDKInput from "@/mdk/MDKInput.vue";
import MDKTagsInput from "@/mdk/MDKTagsInput.vue";
import MDKChart from "@/mdk/MDKChart.vue";

import '@/assets/scss/app.scss';
// @ts-ignore
import VueFlatPickr from 'vue-flatpickr-component';

const app = createApp(App);

app.use(router)
    .use(store)
    // .use(BootstrapVue)
    .use(BootstrapVue3)
    .use(i18n)
    .component("MDKTable", MDKTable)
    .component("MDKForm", MDKForm)
    .component("MDKInput", MDKInput)
    .component("MDKTagsInput", MDKTagsInput)
    .component("MDKChart", MDKChart)
    .component("flatPickr", VueFlatPickr)
    .mount("#app");

/**
 * Vue.prototype is deprecated!!!
 * prototype -> config.globalProperties
 */
app.config.globalProperties.$_app = reactive(new Application(router))

app.config.globalProperties.$MDKOptions = {
    rowFilterPlaceholder: '전체',
    readonlyInputPlaceholder: '-'
}

