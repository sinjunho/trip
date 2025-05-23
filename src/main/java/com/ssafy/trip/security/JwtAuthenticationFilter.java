package com.ssafy.trip.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        log.debug("요청 URL: {}", request.getRequestURI());
        log.debug("Authorization 헤더: {}", requestTokenHeader);
        
        // 옵션 요청은 그냥 통과
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String username = null;
        String jwtToken = null;
        
        // JWT 토큰이 Bearer로 시작하는지 확인
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenProvider.getUsernameFromToken(jwtToken);
                log.debug("토큰에서 추출한 사용자명: {}", username);
            } catch (Exception e) {
                log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            }
        } else {
            log.debug("JWT 토큰이 없거나 Bearer로 시작하지 않음");
        }
        
        // 사용자 인증
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // 토큰이 유효한지 확인
                if (jwtTokenProvider.validateToken(jwtToken)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("사용자 인증 성공: {}, 권한: {}", username, userDetails.getAuthorities());
                } else {
                    log.warn("유효하지 않은 JWT 토큰");
                }
            } catch (Exception e) {
                log.error("사용자 인증 중 오류 발생: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}