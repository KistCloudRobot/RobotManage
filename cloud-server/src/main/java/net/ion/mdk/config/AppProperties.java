package net.ion.mdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties Class
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;

        /**
         * @return String Token
         */
        public String getTokenSecret() {
            return tokenSecret;
        }

        /**
         * @param tokenSecret
         */
        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        /**
         * @return long tokenExpirationMsec
         */
        public long getTokenExpirationMsec() {
            return tokenExpirationMsec;
        }

        /**
         * @param tokenExpirationMsec
         */
        public void setTokenExpirationMsec(long tokenExpirationMsec) {
            this.tokenExpirationMsec = tokenExpirationMsec;
        }
    }

    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        /**
         * @return List<String> authorizedRedirectUris
         */
        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }

    public Auth getAuth() {
        return auth;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }
}
