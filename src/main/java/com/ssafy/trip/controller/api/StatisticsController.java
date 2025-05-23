package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.service.AttractionService;
import com.ssafy.trip.model.service.BoardService;
import com.ssafy.trip.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "통계", description = "전체 통계 정보 API")
public class StatisticsController {
    
    private final AttractionService attractionService;
    private final MemberService memberService;
    private final BoardService boardService;
    
    @GetMapping("/summary")
    @Operation(summary = "전체 통계 요약", description = "사이트 전체 통계 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "통계 조회 성공")
    public ResponseEntity<?> getStatisticsSummary() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 관광지 통계
            try {
                Map<String, Object> attractionStats = attractionService.getStatistics();
                statistics.put("totalAttractions", attractionStats.get("totalCount"));
            } catch (Exception e) {
                log.warn("관광지 통계 조회 실패", e);
                statistics.put("totalAttractions", 1500); // 기본값
            }
            
            // 회원 통계
            try {
                statistics.put("totalMembers", memberService.getTotalMemberCount());
            } catch (Exception e) {
                log.warn("회원 통계 조회 실패", e);
                statistics.put("totalMembers", 10000); // 기본값
            }
            
            // 게시글(후기) 통계
            try {
                statistics.put("totalReviews", boardService.getTotalBoardCount());
            } catch (Exception e) {
                log.warn("게시글 통계 조회 실패", e);
                statistics.put("totalReviews", 5000); // 기본값
            }
            
            // 여행 계획 통계 (구현 예정)
            statistics.put("totalPlans", 3000); // 임시값
            
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            log.error("통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "통계 조회 중 오류 발생: " + e.getMessage()));
        }
    }
}