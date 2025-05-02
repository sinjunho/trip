package com.ssafy.trip.security;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        String requestUri = request.getRequestURI();
        log.debug("Unauthorized request to: {}", requestUri);
        
        // API 요청인 경우 401 Unauthorized 상태 코드 반환
        if (requestUri.startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } 
        // 로그인 페이지 자체에 대한 요청인 경우 리디렉션 방지
        else if ("/member/login".equals(requestUri) || 
                 requestUri.startsWith("/css/") || 
                 requestUri.startsWith("/js/") || 
                 requestUri.startsWith("/img/")) {
            // 정적 리소스 및 로그인 페이지는 통과시킴
            response.setStatus(HttpServletResponse.SC_OK);
        }
        // 웹 페이지 요청인 경우 한 번만 로그인 페이지로 리디렉트
        else {
            // 이미 리디렉션된 요청인지 확인 (무한 리디렉션 방지)
            String redirected = (String) request.getAttribute("redirected");
            if (redirected != null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            } else {
                request.setAttribute("redirected", "true");
                response.sendRedirect(request.getContextPath() + "/member/login");
            }
        }
    }
}