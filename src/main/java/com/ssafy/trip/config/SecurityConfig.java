package com.ssafy.trip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import com.ssafy.trip.security.CustomUserDetailsService;
import com.ssafy.trip.security.JwtAuthenticationFilter;
import com.ssafy.trip.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CorsFilter corsFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스, 홈페이지, 인증 관련 경로 허용
                .requestMatchers("/", "/index", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/member/login", "/member/regist").permitAll()
                
                // API 인증 관련
                .requestMatchers("/api/members/login", "/api/members/register", "/api/members/find-password").permitAll()
                
                // 관광지 조회는 공개
                .requestMatchers(HttpMethod.GET, "/api/attractions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                
                // 챗봇, 통계 공개
                .requestMatchers("/api/chatbot/**").permitAll()
                .requestMatchers("/api/statistics/**").permitAll()
                
                // 관리자 전용 API
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/members/*/role").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/members").hasRole("ADMIN")
                
                // 일반 사용자 인증 필요
                .requestMatchers("/api/members/current", "/api/plans/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/boards/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/boards/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/boards/**").authenticated()
                
             // 댓글 관련 API 권한 설정 추가
                .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/comments/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
             // 공지사항 관련 권한 설정 추가
                .requestMatchers(HttpMethod.GET, "/api/notices/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/notices/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/notices/**").hasRole("ADMIN") 
                .requestMatchers(HttpMethod.DELETE, "/api/notices/**").hasRole("ADMIN")
             // 여행계획 공유
                .requestMatchers(HttpMethod.GET, "/api/planboards/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/planboards/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/planboards/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/planboards/**").authenticated()
                .anyRequest().authenticated()
                
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage("/member/login")
                .defaultSuccessUrl("/")
                .failureUrl("/member/login?error=true")
                .usernameParameter("id")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/access-denied")
            );
            
        return http.build();
    }
}