package com.ssafy.trip.controller.api;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.dto.Comment;
import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.service.CommentService;
import com.ssafy.trip.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글 관리", description = "댓글 관련 API")
public class CommentRestController {
    
    private final CommentService commentService;
    private final MemberService memberService;
    
    @PostMapping
    @Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "댓글 작성 성공")
    public ResponseEntity<?> createComment(
            @Parameter(description = "작성할 댓글 정보", required = true)
            @RequestBody Comment comment, 
            Authentication authentication) {
        
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 정보 설정
            String username = authentication.getName();
            Member member = memberService.selectDetail(username);
            
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            }
            
            comment.setWriter(member.getName());
            
            int result = commentService.createComment(comment);
            
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "댓글이 성공적으로 작성되었습니다.", "commentId", comment.getCommentId()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "댓글 작성에 실패했습니다."));
            }
            
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 작성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/board/{boardId}")
    @Operation(summary = "게시글 댓글 조회", description = "특정 게시글의 댓글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 조회 성공")
    public ResponseEntity<?> getCommentsByBoard(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable int boardId) {
        
        try {
            List<Comment> comments = commentService.getCommentsByBoard(boardId);
            return ResponseEntity.ok(comments);
            
        } catch (Exception e) {
            log.error("댓글 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    public ResponseEntity<?> updateComment(
            @Parameter(description = "수정할 댓글 ID", required = true)
            @PathVariable int commentId,
            @Parameter(description = "수정할 댓글 정보", required = true)
            @RequestBody Comment comment,
            Authentication authentication) {
        
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 기존 댓글 조회
            Comment existingComment = commentService.getCommentById(commentId);
            if (existingComment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "댓글을 찾을 수 없습니다."));
            }
            
            // 권한 확인
            String username = authentication.getName();
            Member member = memberService.selectDetail(username);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!existingComment.getWriter().equals(member.getName()) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "댓글 수정 권한이 없습니다."));
            }
            
            // 댓글 수정
            comment.setCommentId(commentId);
            int result = commentService.updateComment(comment);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 수정되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "댓글 수정에 실패했습니다."));
            }
            
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "삭제할 댓글 ID", required = true)
            @PathVariable int commentId,
            Authentication authentication) {
        
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 기존 댓글 조회
            Comment existingComment = commentService.getCommentById(commentId);
            if (existingComment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "댓글을 찾을 수 없습니다."));
            }
            
            // 권한 확인
            String username = authentication.getName();
            Member member = memberService.selectDetail(username);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!existingComment.getWriter().equals(member.getName()) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "댓글 삭제 권한이 없습니다."));
            }
            
            // 댓글 삭제
            int result = commentService.deleteComment(commentId);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "댓글 삭제에 실패했습니다."));
            }
            
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
}