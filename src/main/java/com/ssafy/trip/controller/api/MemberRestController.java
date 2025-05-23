package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.MemberService;
import com.ssafy.trip.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 관리", description = "회원 정보 관련 API")
public class MemberRestController {
    
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    
    
    @PostMapping("/register")
    @Operation(summary = "회원 등록", description = "새로운 회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<?> register(
            @Parameter(description = "등록할 회원 정보", required = true) 
            @RequestBody Member member) {
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
    @Operation(summary = "로그인", description = "사용자 아이디와 비밀번호로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보", required = true) 
            @RequestBody Map<String, String> loginData) {
        try {
            String id = loginData.get("id");
            String password = loginData.get("password");
            
            Member member = memberService.login(id, password);
            
            if (member != null) {
                // JWT 토큰 생성
                String token = jwtTokenProvider.createToken(member.getId(), member.getRole());
                
                // 응답에서 비밀번호 제거
                member.setPassword(null);
                
                // 디버그 로그 추가
            
                
                // 요청한 형식대로 응답 객체 구성
                Map<String, Object> response = new HashMap<>();
                response.put("user", member);
                response.put("token", token);
                
                log.debug("로그인 성공. 사용자: {}, 역할: {}", member.getId(), member.getRole());
                
                return ResponseEntity.ok(response);
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
    @Operation(summary = "로그아웃", description = "현재 세션을 종료하고 로그아웃합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "회원 정보 조회", description = "특정 ID의 회원 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    public ResponseEntity<?> getMember(
            @Parameter(description = "조회할 회원 ID", required = true) 
            @PathVariable String id) {
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
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    public ResponseEntity<?> updateMember(
            @Parameter(description = "수정할 회원 ID", required = true) 
            @PathVariable String id,
            @Parameter(description = "수정할 회원 정보", required = true) 
            @RequestBody Member memberData,
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 정보 및 권한 확인
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            // 자신의 정보만 수정 가능 (관리자는 모두 가능)
            if (!username.equals(id) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 회원 정보를 수정할 권한이 없습니다."));
            }
            
            // 회원 정보 수정
            memberService.modifyMember(id, memberData.getName(), memberData.getPassword(), 
                                      memberData.getAddress(), memberData.getTel());
            
            // 수정된 회원 정보 조회
            Member updatedMember = memberService.selectDetail(id);
            
            // 응답에서 비밀번호 제거
            if (updatedMember != null) {
                updatedMember.setPassword(null);
            }
            
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 정보 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 삭제 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    public ResponseEntity<?> deleteMember(
            @Parameter(description = "삭제할 회원 ID", required = true) 
            @PathVariable String id,
            @Parameter(description = "삭제 확인을 위한 비밀번호", required = true) 
            @RequestBody Map<String, String> deleteData,
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 정보 및 권한 확인
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            // 자신의 계정만 삭제 가능 (관리자는 모두 가능)
            if (!username.equals(id) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 회원을 삭제할 권한이 없습니다."));
            }
            
            // 비밀번호 확인 (관리자가 아닌 경우)
            if (!isAdmin) {
                String password = deleteData.get("password");
                if (password == null || password.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "비밀번호를 입력해주세요."));
                }
            }
            
            // 회원 삭제
            String password = deleteData.get("password");
            int result = memberService.deleteMember(id, password);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("message", "회원이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원을 삭제하지 못했습니다. 비밀번호를 확인하세요."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "검색 조건에 맞는 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    public ResponseEntity<?> getMembers(
            @Parameter(description = "검색 키") 
            @RequestParam(required = false) String key,
            @Parameter(description = "검색 단어") 
            @RequestParam(required = false) String word,
            @Parameter(description = "현재 페이지") 
            @RequestParam(defaultValue = "1") int currentPage) {
        try {
            SearchCondition condition = new SearchCondition(key, word, currentPage);
            Page<Member> page = memberService.search(condition);
            
            // 각 회원의 비밀번호 정보 제거
            page.getList().forEach(member -> member.setPassword(null));
            
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 목록 조회 중 오류 발생 : " + e.getMessage()));
        }
    }
    
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentMember(Authentication authentication) {
        try {
            log.debug("현재 사용자 정보 요청 - 인증 객체: {}", authentication);
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("인증 정보가 없거나 인증되지 않은 사용자");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "인증되지 않은 사용자입니다."));
            }
            
            String username = authentication.getName();
            log.debug("현재 로그인한 사용자: {}", username);
            
            Member member = memberService.selectDetail(username);
            if (member == null) {
                log.debug("사용자 정보를 찾을 수 없음: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            // 응답에서 민감한 정보 제거
            member.setPassword(null);
            
            log.debug("사용자 정보 조회 성공: {}", member.getId());
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "사용자 정보 조회 중 오류 발생: " + e.getMessage()));
        }
    }
}