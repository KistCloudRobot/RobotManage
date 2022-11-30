<template>
  <RouterView />
</template>

<script>
import appConfig from '@/app.config';
// import { useStore } from 'vuex'
import flatPickr from "flatpickr";
import "flatpickr/dist/flatpickr.css";
import "flatpickr/dist/themes/material_blue.css";

export default {
  name: 'app',
  page: {
    // All subcomponent titles will be injected into this template.
    titleTemplate(title) {
      title = typeof title === "function" ? title(this.$store) : title;
      return title ? `${appConfig.title} | ${title}` : appConfig.title;
    },
  },
  // setup() {
    // const store = useStore();
  // }
  mounted() {
    let dateinputs = [
      'input[type="datetime"]',
    ];
    dateinputs.forEach(function(input) {
      flatPickr(input, {
        dateFormat: "Y-m-d",
        mode: "range",
        allowInput: true
      });
    });

    document.body.setAttribute("data-layout-mode", "light");
    //   // document.querySelector("html").setAttribute('dir', 'rtl');
  }
}
</script>
