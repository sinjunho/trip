// src/main/java/com/ssafy/trip/config/CorsConfig.java
package com.ssafy.trip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 모든 오리진 허용 (개발 환경용)
        config.addAllowedOrigin("http://localhost:5173"); // Vite 개발 서버 포트
        config.addAllowedOrigin("http://localhost:5174"); // Vite 개발 서버 포트
        config.addAllowedOrigin("http://localhost:5175"); // Vite 개발 서버 포트
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:8081");
        
        // 필요한 경우 프로덕션 환경의 도메인도 추가
        // config.addAllowedOrigin("https://your-production-domain.com");
        
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // 쿠키 및 인증 헤더 허용
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}