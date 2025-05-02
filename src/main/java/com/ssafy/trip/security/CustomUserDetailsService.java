package com.ssafy.trip.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.service.MemberService;

import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Member member = memberService.selectDetail(username);
            
            if (member == null) {
                throw new UsernameNotFoundException("User not found with id: " + username);
            }
            
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                member.getRole() != null ? "ROLE_" + member.getRole().toUpperCase() : "ROLE_USER");
            
            return new User(
                member.getId(),
                member.getPassword(),
                Collections.singletonList(authority)
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user by username", e);
        }
    }
}