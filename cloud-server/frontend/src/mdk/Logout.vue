<template>
  <div class="logout-form">
    Logout
  </div>
</template>

<script>
import {JqlApi} from "./jql-api.js";
export default {
  created() {
    this.$parent.appOptions.appEmpty = true;
    this.$_app.logout();
    this.$router.push({name: 'Login'});
  }
}
</script>