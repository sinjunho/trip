package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRestController {
    
    private final MemberService memberService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Member member) {
        try {
            int result = memberService.registMember(member);
            
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "회원이 성공적으로 등록되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원 등록 실패"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 등록 중 오류 발생 : " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        try {
            String id = loginData.get("id");
            String password = loginData.get("password");
            
            Member member = memberService.login(id, password);
            
            if (member != null) {
            	// 응답에서 비밀번호 제거
                member.setPassword(null);
                
                //세션 저장
                session.setAttribute("member", member);
                
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "잘못된 인증"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "로그인 중 오류 : " + e.getMessage()));
        }
    }
    
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMember(@PathVariable String id) {
        try {
            Member member = memberService.selectDetail(id);
            
            if (member != null) {
            	// 응답에서 비밀번호 제거
                member.setPassword(null);
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "맴버를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "멤버를 검색하는 중 오류 발생 :" + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(
            @PathVariable String id,
            @RequestBody Member memberData,
            HttpSession session) {
        try {
        	// 사용자가 승인되었는지 확인
            Member sessionMember = (Member) session.getAttribute("member");
            if (sessionMember == null || !sessionMember.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 회원을 업데이트할 권한이 없습니다."));
            }
            
            memberService.modifyMember(id, memberData.getName(), memberData.getPassword(), 
                                      memberData.getAddress(), memberData.getTel());
            
            // 세션 업데이트
            Member updatedMember = memberService.selectDetail(id);
            session.setAttribute("member", updatedMember);
            
            //  응답에서 비밀번호 제거
            updatedMember.setPassword(null);
            
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 업데이트 중 오류 발생 : " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(
            @PathVariable String id,
            @RequestBody Map<String, String> deleteData,
            HttpSession session) {
        try {
        	// 사용자가 승인되었는지 확인
            Member sessionMember = (Member) session.getAttribute("member");
            if (sessionMember == null || (!sessionMember.getId().equals(id) && 
                !sessionMember.getRole().equals("admin"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 회원을 삭제할 권한이 없습니다."));
            }
            
            String password = deleteData.get("password");
            int result = memberService.deleteMember(id, password);
            
            if (result > 0) {
                // If user deleted themselves, invalidate session
                if (sessionMember.getId().equals(id)) {
                    session.invalidate();
                }
                
                return ResponseEntity.ok(Map.of("message", "회원이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원을 삭제하지 못했습니다. 비밀번호를 확인하세요"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 삭제 중 오류 발생 : " + e.getMessage()));
        }
    }
}