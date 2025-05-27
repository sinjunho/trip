package com.ssafy.trip.controller.api;

import java.sql.SQLException;
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
import com.ssafy.trip.model.dto.Notice;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.MemberService;
import com.ssafy.trip.model.service.NoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "공지사항 관리", description = "공지사항 관련 API")
public class NoticeRestController {
    
    private final NoticeService noticeService;
    private final MemberService memberService;
    
    @GetMapping
    @Operation(summary = "공지사항 목록 조회", description = "검색 조건에 맞는 공지사항 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공")
    public ResponseEntity<?> getNoticeList(
            @Parameter(description = "검색 키") 
            @RequestParam(required = false) String key,
            @Parameter(description = "검색 단어") 
            @RequestParam(required = false) String word,
            @Parameter(description = "현재 페이지") 
            @RequestParam(defaultValue = "1") int currentPage) {
        try {
            SearchCondition condition = new SearchCondition(key, word, currentPage);
            Page<Notice> page = noticeService.search(condition);
            
            return ResponseEntity.ok(page);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "공지사항 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{nno}")
    @Operation(summary = "공지사항 상세 조회", description = "특정 공지사항의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    public ResponseEntity<?> getNoticeDetail(
            @Parameter(description = "조회할 공지사항 번호", required = true) 
            @PathVariable int nno) {
        try {
            Notice notice = noticeService.selectDetail(nno);
            if (notice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "공지사항을 찾을 수 없습니다."));
            }
            
            // 조회수 증가
            noticeService.increaseViewCount(nno);
            
            return ResponseEntity.ok(notice);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "공지사항 상세 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @Operation(summary = "공지사항 작성", description = "새로운 공지사항을 작성합니다. (관리자만 가능)")
    @ApiResponse(responseCode = "201", description = "공지사항 작성 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    public ResponseEntity<?> createNotice(
            @Parameter(description = "작성할 공지사항 정보", required = true) 
            @RequestBody Notice notice, 
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "공지사항을 작성하려면 로그인이 필요합니다."));
            }
            
            // 관리자 권한 확인
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "공지사항 작성은 관리자만 가능합니다."));
            }
            
            // 사용자 정보 가져오기
            String username = authentication.getName();
            Member member = memberService.selectDetail(username);
            
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            // 작성자 설정
            notice.setWriter(member.getName());
            noticeService.writeNotice(notice);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "공지사항이 성공적으로 작성되었습니다."));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "공지사항 작성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{nno}")
    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다. (관리자만 가능)")
    @ApiResponse(responseCode = "200", description = "공지사항 수정 성공")
    @ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    public ResponseEntity<?> updateNotice(
            @PathVariable int nno, 
            @RequestBody Notice notice, 
            Authentication authentication) {
        try {
            // 기존 공지사항 조회
            Notice existingNotice = noticeService.selectDetail(nno);
            if (existingNotice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "공지사항을 찾을 수 없습니다."));
            }
            
            // 관리자 권한 확인
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "공지사항 수정은 관리자만 가능합니다."));
            }
            
            // 공지사항 수정
            notice.setNno(nno);
            noticeService.modifyNotice(notice);
            
            return ResponseEntity.ok(Map.of("message", "공지사항이 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "공지사항 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{nno}")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다. (관리자만 가능)")
    @ApiResponse(responseCode = "200", description = "공지사항 삭제 성공")
    @ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    public ResponseEntity<?> deleteNotice(
            @PathVariable int nno, 
            Authentication authentication) {
        try {
            // 기존 공지사항 조회
            Notice existingNotice = noticeService.selectDetail(nno);
            if (existingNotice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "공지사항을 찾을 수 없습니다."));
            }
            
            // 관리자 권한 확인
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "공지사항 삭제는 관리자만 가능합니다."));
            }
            
            // 공지사항 삭제
            noticeService.deleteNotice(nno);
            
            return ResponseEntity.ok(Map.of("message", "공지사항이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "공지사항 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/important")
    @Operation(summary = "중요 공지사항 조회", description = "중요 공지사항만 조회합니다.")
    @ApiResponse(responseCode = "200", description = "중요 공지사항 조회 성공")
    public ResponseEntity<?> getImportantNotices() {
        try {
            var notices = noticeService.getImportantNotices();
            return ResponseEntity.ok(notices);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "중요 공지사항 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/recent")
    @Operation(summary = "최신 공지사항 조회", description = "최신 공지사항을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "최신 공지사항 조회 성공")
    public ResponseEntity<?> getRecentNotices(
            @Parameter(description = "조회할 개수") 
            @RequestParam(defaultValue = "5") int limit) {
        try {
            var notices = noticeService.getRecentNotices(limit);
            return ResponseEntity.ok(notices);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "최신 공지사항 조회 중 오류 발생: " + e.getMessage()));
        }
    }
}