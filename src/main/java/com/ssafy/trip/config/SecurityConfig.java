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
    private final CorsFilter corsFilter; // 주입
    
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
            // CorsFilter 추가
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf().disable()  // REST API이므로 CSRF 보호 비활성화
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // JWT를 사용하므로 세션 사용 안 함
                .and()
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스, 홈페이지, 인증 관련 경로 허용
                .requestMatchers("/", "/index", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/api/members/current", "/api/plans/**").authenticated()
                .requestMatchers("/member/login", "/member/regist").permitAll()
                
                // API 접근 권한 설정
                .requestMatchers("/api/members/login", "/api/members/register", "/api/members/find-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/attractions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                
                // API 수정, 삭제는 인증 필요
                .requestMatchers(HttpMethod.POST, "/api/boards/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/boards/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/boards/**").authenticated()
                
                // 관광지 관련 페이지는 인증 필요
                .requestMatchers("/attraction/**").authenticated()
                
                // 나머지 요청도 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
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