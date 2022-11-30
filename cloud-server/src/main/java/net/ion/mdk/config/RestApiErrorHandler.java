package net.ion.mdk.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;

/**
 * @Around("execution(* net.ion..*Controller.*(..))") 를 이용한 방식은
 * API의 return-type 과 around 함수의 return-type 이 동일해야만 하는 제약이 있음.
 * @ControllerAdvice 와 @ExceptionHandler 를 이용하여 오류처리를 수행한다.
 */
@RestController
@ControllerAdvice
public class RestApiErrorHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleException(Exception e, WebRequest request) {
        return makeErrorResponse(e);
    }

    private static ResponseEntity<Object> makeErrorResponse(Throwable e) {
        int error = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (e instanceof RestClientResponseException) {
            error = ((RestClientResponseException)e).getRawStatusCode();
        }
        e.printStackTrace();
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return ResponseEntity.status(error)
                .body(newSimpleMap("message", e.getMessage()));
    }

    private static HashMap newSimpleMap(String key, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }



}
