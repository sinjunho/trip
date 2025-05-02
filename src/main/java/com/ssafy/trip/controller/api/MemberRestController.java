package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 관리", description = "회원 정보 관련 API")
public class MemberRestController {
    
    private final MemberService memberService;
    
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
            @RequestBody Map<String, String> loginData, 
            HttpSession session) {
        try {
            String id = loginData.get("id");
            String password = loginData.get("password");
            
            Member member = memberService.login(id, password);
            
            if (member != null) {
                // 응답에서 비밀번호 제거
                member.setPassword(null);
                
                // 세션 저장
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
            
            // 응답에서 비밀번호 제거
            updatedMember.setPassword(null);
            
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 업데이트 중 오류 발생 : " + e.getMessage()));
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
            HttpSession session) {
        try {
            // 사용자가 승인되었는지 확인
//            Member sessionMember = (Member) session.getAttribute("member");
//            if (sessionMember == null || (!sessionMember.getId().equals(id) && 
//                !sessionMember.getRole().equals("admin"))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("message", "이 회원을 삭제할 권한이 없습니다."));
//            }
//            
            String password = deleteData.get("password");
            int result = memberService.deleteMember(id, password);
            
            if (result > 0) {
                // 사용자가 자신을 삭제한 경우 세션 무효화
//                if (sessionMember.getId().equals(id)) {
//                    session.invalidate();
//                }
                
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
}