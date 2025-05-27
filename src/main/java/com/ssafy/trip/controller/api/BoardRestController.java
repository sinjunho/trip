package com.ssafy.trip.controller.api;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ssafy.trip.model.service.BoardService;
import com.ssafy.trip.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "게시판 관리", description = "게시판 관련 API")
public class BoardRestController {
    
    private final BoardService boardService;
    private final MemberService memberService;
    
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "검색 조건에 맞는 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    public ResponseEntity<?> getBoardList(
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
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{bno}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    public ResponseEntity<?> getBoardDetail(
            @Parameter(description = "조회할 게시글 번호", required = true) 
            @PathVariable int bno) {
        try {
            Board board = boardService.selectDetail(bno);
            if (board == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "게시글을 찾을 수 없습니다."));
            }
            
            // 조회수 증가
            boardService.increaseViewCount(bno);
            
            return ResponseEntity.ok(board);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 상세 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 작성 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    public ResponseEntity<?> createBoard(
            @Parameter(description = "작성할 게시글 정보", required = true) 
            @RequestBody Board board, 
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 게시글 작성 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "게시글을 작성하려면 로그인이 필요합니다."));
            }
            
            // 사용자 정보 가져오기
            String username = authentication.getName();
            Member member = memberService.selectDetail(username);
            
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "게시글을 작성하려면 로그인이 필요합니다."));
            }
            
            // 작성자 설정
            board.setWriter(member.getName());
            boardService.writeBoard(board);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "게시글이 성공적으로 작성되었습니다."));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 작성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{bno}")
    public ResponseEntity<?> updateBoard(
            @PathVariable int bno, 
            @RequestBody Board board, 
            Authentication authentication) {
        try {
            // 게시글 조회
            Board existingBoard = boardService.selectDetail(bno);
            if (existingBoard == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "게시글을 찾을 수 없습니다."));
            }
            
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 수정 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 정보 및 권한 확인
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            log.info("게시글 수정 요청 - 작성자: '{}', 현재 사용자: '{}', 관리자: {}", 
                existingBoard.getWriter(), username, isAdmin);
            
            // 작성자 비교 시 공백 제거 및 대소문자 무시
            Member member = memberService.selectDetail(username);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            boolean isAuthor = existingBoard.getWriter().trim().equalsIgnoreCase(member.getName().trim());
            
            if (!isAuthor && !isAdmin) {
                log.warn("권한 없음 - 작성자: '{}', 요청자: '{}'", existingBoard.getWriter(), username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 게시글을 수정할 권한이 없습니다."));
            }
            
            // 게시글 수정 처리
            board.setBno(bno);
            boardService.modifyBoard(board);
            
            log.info("게시글 수정 성공 - 번호: {}", bno);
            return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 수정 중 오류 발생: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{bno}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable int bno, 
            Authentication authentication) {
        try {
            // 게시글 조회
            Board existingBoard = boardService.selectDetail(bno);
            if (existingBoard == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "게시글을 찾을 수 없습니다."));
            }
            
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 삭제 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 정보 및 권한 확인
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            log.info("게시글 삭제 요청 - 작성자: '{}', 현재 사용자: '{}', 관리자: {}", 
                existingBoard.getWriter(), username, isAdmin);
            
            // 작성자 비교
            Member member = memberService.selectDetail(username);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            boolean isAuthor = existingBoard.getWriter().trim().equalsIgnoreCase(member.getName().trim());
            
            if (!isAuthor && !isAdmin) {
                log.warn("권한 없음 - 작성자: '{}', 요청자: '{}'", existingBoard.getWriter(), username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 게시글을 삭제할 권한이 없습니다."));
            }
            
            // 게시글 삭제 처리
            boardService.deleteBoard(bno);
            
            log.info("게시글 삭제 성공 - 번호: {}", bno);
            return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
}