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

import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.dto.TravelPlan;
import com.ssafy.trip.model.service.TravelPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "여행 계획 관리", description = "여행 계획 관련 API")
public class TravelPlanRestController {
    
    private final TravelPlanService travelPlanService;
    
    @PostMapping
    @Operation(summary = "여행 계획 생성", description = "새로운 여행 계획을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "여행 계획 생성 성공")
    public ResponseEntity<?> createPlan(
            @Parameter(description = "생성할 여행 계획 정보", required = true) 
            @RequestBody TravelPlan plan,
            Authentication authentication) {
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 사용자 ID 설정
            plan.setUserId(authentication.getName());
            
            int result = travelPlanService.createPlan(plan);
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "여행 계획이 성공적으로 생성되었습니다.");
                response.put("planId", plan.getPlanId());
                
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "여행 계획 생성에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("여행 계획 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행 계획 생성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "사용자 여행 계획 목록 조회", description = "현재 사용자의 여행 계획 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "여행 계획 목록 조회 성공")
    public ResponseEntity<?> getUserPlans(Authentication authentication) {
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            List<TravelPlan> plans = travelPlanService.getPlansByUserId(authentication.getName());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("여행 계획 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행 계획 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{planId}")
    @Operation(summary = "여행 계획 상세 조회", description = "특정 여행 계획의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "여행 계획 상세 조회 성공")
    public ResponseEntity<?> getPlanDetail(
            @Parameter(description = "조회할 여행 계획 ID", required = true) 
            @PathVariable int planId,
            Authentication authentication) {
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            TravelPlan plan = travelPlanService.getPlanWithDetails(planId);
            
            // 본인의 계획인지 확인
            if (!plan.getUserId().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "접근 권한이 없습니다."));
            }
            
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            log.error("여행 계획 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행 계획 상세 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{planId}")
    @Operation(summary = "여행 계획 수정", description = "여행 계획을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "여행 계획 수정 성공")
    public ResponseEntity<?> updatePlan(
            @Parameter(description = "수정할 여행 계획 ID", required = true) 
            @PathVariable int planId,
            @Parameter(description = "수정할 여행 계획 정보", required = true) 
            @RequestBody TravelPlan plan,
            Authentication authentication) {
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 기존 계획 조회
            TravelPlan existingPlan = travelPlanService.getPlanWithDetails(planId);
            
            // 본인의 계획인지 확인
            if (!existingPlan.getUserId().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "수정 권한이 없습니다."));
            }
            
            plan.setPlanId(planId);
            plan.setUserId(authentication.getName());
            
            int result = travelPlanService.updatePlan(plan);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("message", "여행 계획이 성공적으로 수정되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "여행 계획 수정에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("여행 계획 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행 계획 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{planId}")
    @Operation(summary = "여행 계획 삭제", description = "여행 계획을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "여행 계획 삭제 성공")
    public ResponseEntity<?> deletePlan(
            @Parameter(description = "삭제할 여행 계획 ID", required = true) 
            @PathVariable int planId,
            Authentication authentication) {
        try {
            // 인증 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 기존 계획 조회
            TravelPlan existingPlan = travelPlanService.getPlanWithDetails(planId);
            
            // 본인의 계획인지 확인
            if (!existingPlan.getUserId().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "삭제 권한이 없습니다."));
            }
            
            int result = travelPlanService.deletePlan(planId);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("message", "여행 계획이 성공적으로 삭제되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "여행 계획 삭제에 실패했습니다."));
            }
        } catch (Exception e) {
            log.error("여행 계획 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행 계획 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    

}