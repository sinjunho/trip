package com.ssafy.trip.controller.api;

import java.util.List;
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
import com.ssafy.trip.model.dto.TravelPlan;
import com.ssafy.trip.model.service.TravelPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "여행 계획", description = "여행 계획 관련 API")
public class TravelPlanRestController {
    
    private final TravelPlanService planService;
    
    @PostMapping
    @Operation(summary = "여행 계획 생성", description = "새로운 여행 계획을 생성합니다.")
    public ResponseEntity<?> createPlan(
            @RequestBody TravelPlan plan,
            HttpSession session) {
        try {
            Member loginMember = (Member) session.getAttribute("member");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            plan.setUserId(loginMember.getId());
            int result = planService.createPlan(plan);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("planId", plan.getPlanId(), "message", "여행 계획이 생성되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "여행 계획 생성 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "여행 계획 목록 조회", description = "사용자의 여행 계획 목록을 조회합니다.")
    public ResponseEntity<?> getPlanList(HttpSession session) {
        try {
            Member loginMember = (Member) session.getAttribute("member");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            List<TravelPlan> plans = planService.getPlansByUserId(loginMember.getId());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "여행 계획 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{planId}")
    @Operation(summary = "여행 계획 상세 조회", description = "특정 여행 계획의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getPlanDetail(@PathVariable int planId, HttpSession session) {
        try {
            Member loginMember = (Member) session.getAttribute("member");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            TravelPlan plan = planService.getPlanWithDetails(planId);
            
            // 권한 확인 (자신의 계획인지)
            if (!plan.getUserId().equals(loginMember.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "접근 권한이 없습니다."));
            }
            
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "여행 계획 상세 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{planId}")
    @Operation(summary = "여행 계획 수정", description = "여행 계획을 수정합니다.")
    public ResponseEntity<?> updatePlan(
            @PathVariable int planId,
            @RequestBody TravelPlan plan,
            HttpSession session) {
        try {
            Member loginMember = (Member) session.getAttribute("member");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 권한 확인
            TravelPlan existingPlan = planService.getPlanWithDetails(planId);
            if (!existingPlan.getUserId().equals(loginMember.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "접근 권한이 없습니다."));
            }
            
            plan.setPlanId(planId);
            plan.setUserId(loginMember.getId());
            int result = planService.updatePlan(plan);
            
            return ResponseEntity.ok(Map.of("message", "여행 계획이 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "여행 계획 수정 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{planId}")
    @Operation(summary = "여행 계획 삭제", description = "여행 계획을 삭제합니다.")
    public ResponseEntity<?> deletePlan(@PathVariable int planId, HttpSession session) {
        try {
            Member loginMember = (Member) session.getAttribute("member");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            // 권한 확인
            TravelPlan existingPlan = planService.getPlanWithDetails(planId);
            if (!existingPlan.getUserId().equals(loginMember.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "접근 권한이 없습니다."));
            }
            
            int result = planService.deletePlan(planId);
            
            return ResponseEntity.ok(Map.of("message", "여행 계획이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "여행 계획 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/share/{planId}")
    @Operation(summary = "공유된 여행 계획 조회", description = "공유된 여행 계획을 조회합니다.")
    public ResponseEntity<?> getSharedPlan(@PathVariable int planId) {
        try {
            TravelPlan plan = planService.getPlanWithDetails(planId);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공유된 여행 계획 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{planId}/copy")
    @Operation(summary = "여행 계획 복사", description = "다른 사용자의 여행 계획을 복사합니다.")
    public ResponseEntity<?> copyPlan(@PathVariable int planId, HttpSession session) {
        try {
            Member loginMember = (Member) session.getAttribute("member");
            if (loginMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }
            
            TravelPlan originalPlan = planService.getPlanWithDetails(planId);
            originalPlan.setPlanId(0); // 새 ID 할당 위해 0으로 설정
            originalPlan.setTitle(originalPlan.getTitle() + " (복사본)");
            originalPlan.setUserId(loginMember.getId());
            
            int result = planService.createPlan(originalPlan);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("planId", originalPlan.getPlanId(), "message", "여행 계획이 복사되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "여행 계획 복사 중 오류 발생: " + e.getMessage()));
        }
    }
}