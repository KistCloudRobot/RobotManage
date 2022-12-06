package net.ion.mdk.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom Exception - OAuth2AuthenticationProcessing
 */
public class OAuth2AuthenticationProcessingException extends AuthenticationException {
    public OAuth2AuthenticationProcessingException(String msg, Throwable t) {
        super(msg, t);
    }

    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
