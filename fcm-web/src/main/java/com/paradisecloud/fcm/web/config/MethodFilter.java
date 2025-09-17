package com.paradisecloud.fcm.web.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MethodFilter  extends OncePerRequestFilter {

    public static final String OPTIONS = "OPTIONS";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equals(OPTIONS)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
