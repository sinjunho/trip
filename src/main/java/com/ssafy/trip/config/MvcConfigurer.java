package com.ssafy.trip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.trip.interceptor.SessionInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MvcConfigurer implements WebMvcConfigurer {
   // private final PerformanceInterceptor pi;
    private final SessionInterceptor si;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(pi);
        registry.addInterceptor(si).addPathPatterns("/movie/regist").excludePathPatterns("/movie/movie-list");
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/auth/help").setViewName("help/auth-help");
//    }
//
//    @Value("${spring.servlet.multipart.location}")
//    String filePath;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + filePath + "/");
//    }
}
