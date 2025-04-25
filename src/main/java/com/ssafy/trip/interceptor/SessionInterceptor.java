package com.ssafy.trip.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("loginUser") != null) {
            return true;
        } else {
            session.setAttribute("alertMsg", "로그인 후 사용하세요.");
            response.sendRedirect(request.getContextPath() + "/member/login-form");
            return false;
        }
    }
}
