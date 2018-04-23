package com.laile.esf.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class Describe
 * <p>
 * User: yangguang Date: 17/7/18 Time: 下午6:17
 */
public class ForceNoCacheFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ((HttpServletResponse) response).setHeader("Cache-Control", "no-cache");
        ((HttpServletResponse) response).setHeader("Pragma", "no-cache");
        ((HttpServletResponse) response).setDateHeader("Expires", -1L);
        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
