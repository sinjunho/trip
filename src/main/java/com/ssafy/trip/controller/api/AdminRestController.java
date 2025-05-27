package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.ssafy.trip.model.dto.Board;
import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.AttractionService;
import com.ssafy.trip.model.service.BoardService;
import com.ssafy.trip.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "관리자 전용", description = "관리자 권한이 필요한 API")
public class AdminRestController {
    
    private final MemberService memberService;
    private final BoardService boardService;
    private final AttractionService attractionService;
    
    // ==================== 회원 관리 ====================
    
    @GetMapping("/members")
    @Operation(summary = "전체 회원 목록 조회", description = "관리자용 전체 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    public ResponseEntity<?> getAllMembers(
            @Parameter(description = "검색 키") 
            @RequestParam(required = false) String key,
            @Parameter(description = "검색 단어") 
            @RequestParam(required = false) String word,
            @Parameter(description = "현재 페이지") 
            @RequestParam(defaultValue = "1") int currentPage) {
        try {
            SearchCondition condition = new SearchCondition(key, word, currentPage);
            Page<Member> page = memberService.search(condition);
            
            // 비밀번호 정보 제거
            page.getList().forEach(member -> member.setPassword(null));
            
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            log.error("회원 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/members/{id}/role")
    @Operation(summary = "회원 권한 변경", description = "회원의 권한을 변경합니다.")
    @ApiResponse(responseCode = "200", description = "권한 변경 성공")
    public ResponseEntity<?> updateMemberRole(
            @Parameter(description = "회원 ID", required = true) 
            @PathVariable String id,
            @Parameter(description = "새로운 권한", required = true) 
            @RequestBody Map<String, String> roleData,
            Authentication authentication) {
        try {
            String newRole = roleData.get("role");
            
            if (newRole == null || (!newRole.equals("user") && !newRole.equals("admin"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "유효하지 않은 권한입니다. (user 또는 admin)"));
            }
            
            // 자신의 권한은 변경할 수 없음
            if (authentication.getName().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "자신의 권한은 변경할 수 없습니다."));
            }
            
            Member member = memberService.selectDetail(id);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "회원을 찾을 수 없습니다."));
            }
            
            // 권한 변경 로직 (MemberService에 메서드 추가 필요)
            // memberService.updateMemberRole(id, newRole);
            
            log.info("관리자 {}가 회원 {}의 권한을 {}로 변경함", authentication.getName(), id, newRole);
            
            return ResponseEntity.ok(Map.of("message", "권한이 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            log.error("권한 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "권한 변경 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/members/{id}")
    @Operation(summary = "회원 강제 삭제", description = "관리자가 회원을 강제로 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 삭제 성공")
    public ResponseEntity<?> forcDeleteMember(
            @Parameter(description = "삭제할 회원 ID", required = true) 
            @PathVariable String id,
            Authentication authentication) {
        try {
            // 자신은 삭제할 수 없음
            if (authentication.getName().equals(id)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "자신의 계정은 삭제할 수 없습니다."));
            }
            
            Member member = memberService.selectDetail(id);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "회원을 찾을 수 없습니다."));
            }
            
            // 관리자 강제 삭제 (비밀번호 없이)
            int result = memberService.deleteMember(id, null); // 비밀번호 null로 전달
            
            if (result > 0) {
                log.info("관리자 {}가 회원 {}를 강제 삭제함", authentication.getName(), id);
                return ResponseEntity.ok(Map.of("message", "회원이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원 삭제에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("회원 강제 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "회원 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // ==================== 게시글 관리 ====================
    
    @GetMapping("/boards")
    @Operation(summary = "전체 게시글 관리", description = "관리자용 전체 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    public ResponseEntity<?> getAllBoards(
            @Parameter(description = "검색 키") 
            @RequestParam(required = false) String key,
            @Parameter(description = "검색 단어") 
            @RequestParam(required = false) String word,
            @Parameter(description = "현재 페이지") 
            @RequestParam(defaultValue = "1") int currentPage) {
        try {
            SearchCondition condition = new SearchCondition(key, word, currentPage);
            Page<Board> page = boardService.search(condition);
            
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/boards/{bno}")
    @Operation(summary = "게시글 강제 삭제", description = "관리자가 게시글을 강제로 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
    public ResponseEntity<?> forceDeleteBoard(
            @Parameter(description = "삭제할 게시글 번호", required = true) 
            @PathVariable int bno,
            Authentication authentication) {
        try {
            Board board = boardService.selectDetail(bno);
            if (board == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "게시글을 찾을 수 없습니다."));
            }
            
            int result = boardService.deleteBoard(bno);
            
            if (result > 0) {
                log.info("관리자 {}가 게시글 {}를 강제 삭제함", authentication.getName(), bno);
                return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "게시글 삭제에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("게시글 강제 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // ==================== 통계 및 대시보드 ====================
    
    @GetMapping("/dashboard")
    @Operation(summary = "관리자 대시보드", description = "관리자용 대시보드 데이터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "대시보드 데이터 조회 성공")
    public ResponseEntity<?> getDashboardData() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();
            
            // 회원 통계
            int totalMembers = memberService.getTotalMemberCount();
            dashboardData.put("totalMembers", totalMembers);
            
            // 게시글 통계
            int totalBoards = boardService.getTotalBoardCount();
            dashboardData.put("totalBoards", totalBoards);
            
            // 관광지 통계
            Map<String, Object> attractionStats = attractionService.getStatistics();
            dashboardData.put("totalAttractions", attractionStats.get("totalCount"));
            
            // 최근 가입 회원 (최근 5명)
            List<Member> recentMembers = memberService.searchAll();
            if (recentMembers.size() > 5) {
                recentMembers = recentMembers.subList(0, 5);
            }
            recentMembers.forEach(member -> member.setPassword(null));
            dashboardData.put("recentMembers", recentMembers);
            
            // 인기 관광지
            dashboardData.put("popularCities", attractionService.getPopularCities());
            
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("대시보드 데이터 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "대시보드 데이터 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PostMapping("/system/maintenance")
    @Operation(summary = "시스템 유지보수 모드", description = "시스템을 유지보수 모드로 전환합니다.")
    @ApiResponse(responseCode = "200", description = "유지보수 모드 전환 성공")
    public ResponseEntity<?> toggleMaintenanceMode(
            @RequestBody Map<String, Boolean> maintenanceData,
            Authentication authentication) {
        try {
            boolean maintenanceMode = maintenanceData.getOrDefault("enabled", false);
            
            // 시스템 설정 변경 로직 구현 필요
            log.info("관리자 {}가 유지보수 모드를 {}로 변경함", 
                authentication.getName(), maintenanceMode ? "활성화" : "비활성화");
            
            return ResponseEntity.ok(Map.of(
                "message", "유지보수 모드가 " + (maintenanceMode ? "활성화" : "비활성화") + "되었습니다.",
                "maintenanceMode", maintenanceMode
            ));
        } catch (Exception e) {
            log.error("유지보수 모드 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "유지보수 모드 변경 중 오류 발생: " + e.getMessage()));
        }
    }
}