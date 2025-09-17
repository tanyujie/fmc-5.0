package com.paradisecloud.fcm.web.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nj
 * @date 2022/7/27 11:55
 */
@Component
@WebFilter(filterName = "MyWebFilter",
        urlPatterns = "/*"
)
public class MyWebFilter implements Filter {

    public static final String OPTIONS = "OPTIONS";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        if (request.getMethod().equals(OPTIONS)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        response.addHeader("X-Frame-Options","SAMEORIGIN");
        response.addHeader("Referrer-Policy","origin");
        response.addHeader("Content-Security-Policy","object-src 'self'");
        response.addHeader("X-Permitted-Cross-Domain-Policies","master-only");
        response.addHeader("X-Content-Type-Options","nosniff");
        response.addHeader("X-XSS-Protection","1; mode=block");
        response.addHeader("X-Download-Options","noopen");
        response.addHeader("Strict-Transport-Security","max-age=61536000; includeSubdomains; preload");
        //处理cookie问题
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String value = cookie.getValue();
                StringBuilder builder = new StringBuilder();
                builder.append(cookie.getName()+"="+value+";");
                builder.append("Secure;");
                builder.append("HttpOnly;");
                response.addHeader("Set-Cookie", builder.toString());
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
