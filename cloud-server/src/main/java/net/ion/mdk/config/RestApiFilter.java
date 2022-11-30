package net.ion.mdk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * WebFilter 예제.
 * @Component
 * @WebFilter(urlPatterns = {
 *         "/api/v2/*", "/rest/api/*"
 * })
 */
public class RestApiFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RestApiFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init XSSFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        log.info("##### filter - before #####");
        chain.doFilter(req, res);
        log.info("##### filter - after #####");
    }

    @Override
    public void destroy() {
        log.info("destroy XSSFilter");
    }

}
