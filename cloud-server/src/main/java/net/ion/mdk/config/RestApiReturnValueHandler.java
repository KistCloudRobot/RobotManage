package net.ion.mdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ion.mdk.util.ClassUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReturnValueHandler sample
 * @zee 2022-04-14 현재 Sample Handler 는 동작되지 않음.
 *  -> CustomHandler 는 다른 handler 보다 우선 순위가 낮아서, 특정 type 외에는 Hooking 불가함.
 */
public class RestApiReturnValueHandler extends HandlerMethodReturnValueHandlerComposite {

    private final ApplicationContext context;
    private boolean initialized = false;

    public RestApiReturnValueHandler(ApplicationContext context) {
        this.context = context;
    }

    void init() {
        if (initialized) return;
        initialized = true;
        RequestMappingHandlerAdapter requestAdapter = context.getBean(RequestMappingHandlerAdapter.class);
        for (HandlerMethodReturnValueHandler handler : requestAdapter.getReturnValueHandlers()) {
            super.addHandler(handler);
        }
    }

    @Override
    public boolean supportsReturnType(MethodParameter param) {
        init();

        Class<?> returnType = param.getParameterType();
        if (ResponseEntity.class.isAssignableFrom(returnType)) return false;
        if (Map.class.isAssignableFrom(returnType)) return true;
        if (ClassUtils.findPublicField(returnType, "content") != null) return false;
        if (ClassUtils.findPublicMethod(returnType, "getContent") != null) return false;
        return true;
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        if (o instanceof Map && ((Map)o).get("content") != null) {
            // do nothing
        }
        else {
            HashMap<Object, Object> res = new HashMap<>();
            res.put("content", o);
            o = res;
        }
        super.handleReturnValue(o, methodParameter, modelAndViewContainer, nativeWebRequest);
    }
}
