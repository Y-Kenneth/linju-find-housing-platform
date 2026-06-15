package com.linjufind.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // Whitelist — always allowed through without login
        if (uri.contains("/login")
                || uri.contains("/register")
                || uri.contains("/css/")
                || uri.contains("/js/")
                || uri.contains("/images/")) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        Object loginUser = req.getSession().getAttribute("loginUser");
        if (loginUser instanceof com.linjufind.entity.User user) {
            if ("deactivated".equals(user.getRole())) {
                req.getSession().invalidate();
                resp.sendRedirect("/login");
                return;
            }
            chain.doFilter(request, response);
        } else if (loginUser != null) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect("/login");
        }
    }
}
