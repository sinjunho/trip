package com.ssafy.trip.controller.api;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.PlanBoard;
import com.ssafy.trip.model.dto.PlanBoardComment;
import com.ssafy.trip.model.dto.PlanBoardSearchCondition;
import com.ssafy.trip.model.dto.PlanBoardSummary;
import com.ssafy.trip.model.dto.PlanBoardTag;
import com.ssafy.trip.model.service.MemberService;
import com.ssafy.trip.model.service.PlanBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/planboards")
@RequiredArgsConstructor
@Tag(name = "여행 공유 게시판", description = "여행 계획 공유 게시판 관련 API")
public class PlanBoardRestController {
    
    private final PlanBoardService planBoardService;
    private final MemberService memberService;
    
    // ======================== 게시글 CRUD ========================
    
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "검색 조건에 맞는 여행 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    public ResponseEntity<?> getPlanBoardList(
            @Parameter(description = "검색 키") @RequestParam(required = false) String key,
            @Parameter(description = "검색 단어") @RequestParam(required = false) String word,
            @Parameter(description = "현재 페이지") @RequestParam(defaultValue = "1") int currentPage,
            @Parameter(description = "여행 테마") @RequestParam(required = false) String travelTheme,
            @Parameter(description = "목적지") @RequestParam(required = false) String destination,
            @Parameter(description = "정렬 기준 (latest, popular, likes)") @RequestParam(defaultValue = "latest") String sortBy,
            @Parameter(description = "추천 게시글만 보기") @RequestParam(defaultValue = "false") boolean onlyFeatured,
            @Parameter(description = "태그명") @RequestParam(required = false) String tagName,
            @Parameter(description = "내 게시글만 보기") @RequestParam(defaultValue = "false") boolean onlyMyPosts,
            Authentication authentication) {
        try {
            // 검색 조건 생성
            PlanBoardSearchCondition condition = new PlanBoardSearchCondition(key, word, currentPage);
            condition.setTravelTheme(travelTheme);
            condition.setDestination(destination);
            condition.setSortBy(sortBy);
            condition.setOnlyFeatured(onlyFeatured);
            condition.setTagName(tagName);
            condition.setOnlyMyPosts(onlyMyPosts);
            
            // 현재 사용자 ID 설정 (좋아요 표시 여부 확인용)
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                condition.setUserId(userId);
                
                // 내 게시글만 보기 요청 시 권한 확인
                if (onlyMyPosts && userId == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
                }
            } else if (onlyMyPosts) {
                // 로그인하지 않은 상태에서 내 게시글만 보기 요청 시
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 게시글 목록 조회
            List<PlanBoardSummary> planBoards = planBoardService.getPlanBoardList(condition);
            
            // 전체 게시글 수 조회
            int totalCount = planBoardService.getTotalPlanBoardCount(condition);
            
            // 페이지 정보 구성
            Map<String, Object> response = new HashMap<>();
            response.put("list", planBoards);
            response.put("totalCount", totalCount);
            response.put("currentPage", condition.getCurrentPage());
            response.put("totalPages", (int) Math.ceil((double) totalCount / condition.getItemsPerPage()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{pboardNo}")
    @Operation(summary = "게시글 상세 조회", description = "특정 여행 게시글의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    public ResponseEntity<?> getPlanBoardDetail(
            @Parameter(description = "조회할 게시글 번호", required = true) @PathVariable int pboardNo,
            Authentication authentication) {
        try {
            // 인증 정보에서 사용자 ID 추출 (없으면 null)
            String userId = null;
            if (authentication != null && authentication.isAuthenticated()) {
                userId = authentication.getName();
            }
            
            // 게시글 상세 조회
            PlanBoard planBoard = planBoardService.getPlanBoardDetail(pboardNo, userId);
            
            if (planBoard == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "게시글을 찾을 수 없습니다."));
            }
            
            // 조회수 증가
            planBoardService.increaseViewCount(pboardNo);
            
            return ResponseEntity.ok(planBoard);
        } catch (Exception e) {
            log.error("게시글 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 상세 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @Operation(summary = "게시글 작성", description = "새로운 여행 게시글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 작성 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    public ResponseEntity<?> createPlanBoard(
            @Parameter(description = "작성할 게시글 정보", required = true) @RequestBody PlanBoard planBoard,
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
            
            // 작성자 정보 설정
            planBoard.setUserId(member.getId());
            planBoard.setWriter(member.getName());
            
            // 🔥 isPublic 값 확인 및 기본값 설정
            if (planBoard.getIsPublic() == null) {
                planBoard.setIsPublic(true); // 기본값: 공개
            }
            
            log.debug("게시글 작성 - isPublic 값: {}", planBoard.getIsPublic());
            
            // 게시글 등록
            int result = planBoardService.createPlanBoard(planBoard);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "pboardNo", planBoard.getPboardNo(),
                    "message", "게시글이 성공적으로 작성되었습니다."
                ));
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 작성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{pboardNo}")
    @Operation(summary = "게시글 수정", description = "여행 게시글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    public ResponseEntity<?> updatePlanBoard(
            @Parameter(description = "수정할 게시글 번호", required = true) @PathVariable int pboardNo,
            @Parameter(description = "수정할 게시글 정보", required = true) @RequestBody PlanBoard planBoard,
            Authentication authentication) {
        try {
            // 게시글 조회
            PlanBoard existingBoard = planBoardService.getPlanBoardDetail(pboardNo, null);
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
            
            // 작성자 확인
            if (!existingBoard.getUserId().equals(username) && !isAdmin) {
                log.warn("권한 없음 - 작성자: '{}', 요청자: '{}'", existingBoard.getUserId(), username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 게시글을 수정할 권한이 없습니다."));
            }
            
            // 게시글 번호 설정
            planBoard.setPboardNo(pboardNo);
            
            // 게시글 수정
            int result = planBoardService.updatePlanBoard(planBoard);
            
            return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{pboardNo}")
    @Operation(summary = "게시글 삭제", description = "여행 게시글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    public ResponseEntity<?> deletePlanBoard(
            @Parameter(description = "삭제할 게시글 번호", required = true) @PathVariable int pboardNo,
            Authentication authentication) {
        try {
            // 게시글 조회
            PlanBoard existingBoard = planBoardService.getPlanBoardDetail(pboardNo, null);
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
            
            // 작성자 확인
            if (!existingBoard.getUserId().equals(username) && !isAdmin) {
                log.warn("권한 없음 - 작성자: '{}', 요청자: '{}'", existingBoard.getUserId(), username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 게시글을 삭제할 권한이 없습니다."));
            }
            
            // 게시글 삭제
            int result = planBoardService.deletePlanBoard(pboardNo);
            
            return ResponseEntity.ok(Map.of("message", "게시글이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/featured")
    @Operation(summary = "추천 게시글 목록 조회", description = "추천된 여행 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "추천 게시글 조회 성공")
    public ResponseEntity<?> getFeaturedPlanBoards(
            @Parameter(description = "조회할 게시글 수") @RequestParam(defaultValue = "6") int limit) {
        try {
            List<PlanBoardSummary> featuredPlanBoards = planBoardService.getFeaturedPlanBoards(limit);
            return ResponseEntity.ok(featuredPlanBoards);
        } catch (Exception e) {
            log.error("추천 게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "추천 게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 목록 조회", description = "인기 있는 여행 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 게시글 조회 성공")
    public ResponseEntity<?> getPopularPlanBoards(
            @Parameter(description = "조회할 게시글 수") @RequestParam(defaultValue = "6") int limit) {
        try {
            List<PlanBoardSummary> popularPlanBoards = planBoardService.getPopularPlanBoards(limit);
            return ResponseEntity.ok(popularPlanBoards);
        } catch (Exception e) {
            log.error("인기 게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "인기 게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 게시글 목록 조회", description = "특정 사용자의 여행 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자별 게시글 조회 성공")
    public ResponseEntity<?> getUserPlanBoards(
            @Parameter(description = "조회할 사용자 ID", required = true) @PathVariable String userId,
            @Parameter(description = "조회 시작 위치") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "조회할 게시글 수") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<PlanBoardSummary> userPlanBoards = planBoardService.getPlanBoardsByUserId(userId, offset, limit);
            return ResponseEntity.ok(userPlanBoards);
        } catch (Exception e) {
            log.error("사용자별 게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "사용자별 게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // ======================== 댓글 관리 ========================
    
    @GetMapping("/{pboardNo}/comments")
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    public ResponseEntity<?> getComments(
            @Parameter(description = "조회할 게시글 번호", required = true) @PathVariable int pboardNo) {
        try {
            List<PlanBoardComment> comments = planBoardService.getCommentsByPboardNo(pboardNo);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("댓글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{pboardNo}/comments")
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "댓글 작성 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    public ResponseEntity<?> createComment(
            @Parameter(description = "댓글을 작성할 게시글 번호", required = true) @PathVariable int pboardNo,
            @Parameter(description = "작성할 댓글 정보", required = true) @RequestBody PlanBoardComment comment,
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 댓글 작성 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "댓글을 작성하려면 로그인이 필요합니다."));
            }
            
            // 사용자 정보 가져오기
            String username = authentication.getName();
            Member member = memberService.selectDetail(username);
            
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "댓글을 작성하려면 로그인이 필요합니다."));
            }
            
            // 댓글 정보 설정
            comment.setPboardNo(pboardNo);
            comment.setUserId(member.getId());
            comment.setWriter(member.getName());
            
            // 댓글 등록
            int result = planBoardService.createComment(comment);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "commentId", comment.getCommentId(),
                    "message", "댓글이 성공적으로 작성되었습니다."
                ));
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 작성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    public ResponseEntity<?> updateComment(
            @Parameter(description = "수정할 댓글 번호", required = true) @PathVariable int commentId,
            @Parameter(description = "수정할 댓글 정보", required = true) @RequestBody PlanBoardComment comment,
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 댓글 수정 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 정보 가져오기
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            // 댓글 번호 설정
            comment.setCommentId(commentId);
            
            // 권한 확인 (관리자 또는 작성자)
            if (!comment.getUserId().equals(username) && !isAdmin) {
                log.warn("권한 없음 - 댓글 작성자: '{}', 요청자: '{}'", comment.getUserId(), username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "이 댓글을 수정할 권한이 없습니다."));
            }
            
            // 댓글 수정
            int result = planBoardService.updateComment(comment);
            
            return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "삭제할 댓글 번호", required = true) @PathVariable int commentId,
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 댓글 삭제 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 권한 확인 (향후 구현)
            // ...
            
            // 댓글 삭제
            int result = planBoardService.deleteComment(commentId);
            
            return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "댓글 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // ======================== 좋아요 관리 ========================
    
    @PostMapping("/{pboardNo}/likes")
    @Operation(summary = "좋아요 토글", description = "게시글 좋아요를 추가하거나 취소합니다.")
    @ApiResponse(responseCode = "200", description = "좋아요 토글 성공")
    @ApiResponse(responseCode = "401", description = "로그인 필요")
    public ResponseEntity<?> toggleLike(
            @Parameter(description = "좋아요 토글할 게시글 번호", required = true) @PathVariable int pboardNo,
            Authentication authentication) {
        try {
            // 인증 정보 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("인증되지 않은 사용자의 좋아요 시도");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "좋아요를 하려면 로그인이 필요합니다."));
            }
            
            // 사용자 정보 가져오기
            String userId = authentication.getName();
            
            // 좋아요 토글
            boolean isLiked = planBoardService.toggleLike(pboardNo, userId);
            
            return ResponseEntity.ok(Map.of(
                "isLiked", isLiked,
                "message", isLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다."
            ));
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "좋아요 토글 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // ======================== 태그 관리 ========================
    
    @GetMapping("/tags/popular")
    @Operation(summary = "인기 태그 목록 조회", description = "인기 있는 태그 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 태그 조회 성공")
    public ResponseEntity<?> getPopularTags(
            @Parameter(description = "조회할 태그 수") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<PlanBoardTag> popularTags = planBoardService.getPopularTags(limit);
            return ResponseEntity.ok(popularTags);
        } catch (Exception e) {
            log.error("인기 태그 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "인기 태그 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tags/{tagName}")
    @Operation(summary = "태그별 게시글 목록 조회", description = "특정 태그가 포함된 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "태그별 게시글 조회 성공")
    public ResponseEntity<?> getPlanBoardsByTag(
            @Parameter(description = "조회할 태그명", required = true) @PathVariable String tagName,
            @Parameter(description = "조회 시작 위치") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "조회할 게시글 수") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<PlanBoardSummary> taggedPlanBoards = planBoardService.getPlanBoardsByTag(tagName, offset, limit);
            return ResponseEntity.ok(taggedPlanBoards);
        } catch (Exception e) {
            log.error("태그별 게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "태그별 게시글 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    // ======================== 통계 및 기타 ========================
    
    @GetMapping("/statistics/themes")
    @Operation(summary = "여행 테마별 통계 조회", description = "여행 테마별 통계 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "여행 테마별 통계 조회 성공")
    public ResponseEntity<?> getTravelThemeStatistics() {
        try {
            List<Map<String, Object>> themeStatistics = planBoardService.getTravelThemeStatistics();
            return ResponseEntity.ok(themeStatistics);
        } catch (Exception e) {
            log.error("여행 테마별 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행 테마별 통계 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics/monthly")
    @Operation(summary = "월별 게시글 통계 조회", description = "월별 게시글 통계 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "월별 게시글 통계 조회 성공")
    public ResponseEntity<?> getMonthlyStatistics(
            @Parameter(description = "조회할 년도") @RequestParam(defaultValue = "2024") int year) {
        try {
            List<Map<String, Object>> monthlyStatistics = planBoardService.getMonthlyStatistics(year);
            return ResponseEntity.ok(monthlyStatistics);
        } catch (Exception e) {
            log.error("월별 게시글 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "월별 게시글 통계 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 검색 성공")
    public ResponseEntity<?> searchPlanBoards(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(description = "검색 타입 (title, content, writer, destination, all)") 
            @RequestParam(defaultValue = "all") String searchType,
            @Parameter(description = "조회 시작 위치") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "조회할 게시글 수") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<PlanBoardSummary> searchResults = planBoardService.searchPlanBoards(keyword, searchType, offset, limit);
            int totalCount = planBoardService.getSearchResultCount(keyword, searchType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("results", searchResults);
            response.put("totalCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("게시글 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "게시글 검색 중 오류 발생: " + e.getMessage()));
        }
    }
}