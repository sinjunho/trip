package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.service.MemberService;
import com.ssafy.trip.security.CustomUserDetailsService;
import com.ssafy.trip.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, String> authRequest) throws Exception {
        try {
            // Spring Security 인증
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.get("id"),
                    authRequest.get("password")
                )
            );
            
            // 토큰 생성
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.get("id"));
            final String token = jwtTokenUtil.generateToken(userDetails);
            
            // 사용자 정보 조회
            Member member = memberService.selectDetail(authRequest.get("id"));
            
            // 민감 정보 제거
            member.setPassword(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("member", member);
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid username or password", 
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Member member) {
        try {
            int result = memberService.registMember(member);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("message", "User registered successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Failed to register user"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Registration failed", 
                "error", e.getMessage()
            ));
        }
    }
}