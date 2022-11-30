const { defineConfig } = require("@vue/cli-service");
const path = require('path')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': { // for @may api proxy 확인 plz - @yeony 22.08.11
        target: 'http://localhost:6090',
        changeOrigin: true,
        secure: false
      },
      // MDK-OAuth. @zee OAuth 대신에 Local 인증(Email+암호) 사용 시 서버 호출용 API (AuthController.java 참조)
      '/auth': {
        target: 'http://localhost:6090', // this configuration needs to correspond to the Spring Boot backends' application.yaml server.port
        ws: true,
        changeOrigin: true
      }
    }
  },
  outputDir: path.resolve(__dirname, "../" + "src/main/resources/static"),
  publicPath: '/',
  assetsDir: 'static'
});

