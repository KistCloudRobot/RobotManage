<template>
  <div class="oauth2-redirect">
    Authentication Success --------------------
  </div>
</template>

<script>

export default {
  methods : {
    getUrlParameter(name) {
      name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
      const regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

      const results = regex.exec(document.location.search);
      return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    }
  },

  created() {
    console.log("---------------- OAuth2RedirectHandler ----------------");
    const token = this.getUrlParameter('token');
    const error = this.getUrlParameter('error');

    console.log('token : ' + token);
    console.log('error : ' + error);

    // @zee oauth token 설정.
    this.$_app.setAccessToken(token);
    if (error) {
      alert(error);
    }
    // router.push() 를 사용하면, 화면 갱신이 제대로 되지 않는 문제가 있다.
    window.location.href = "/";
  }
}
</script>