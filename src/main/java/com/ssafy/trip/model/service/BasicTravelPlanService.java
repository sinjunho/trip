package com.ssafy.trip.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.trip.exception.RecordNotFoundException;
import com.ssafy.trip.model.dao.AttractionDao;
import com.ssafy.trip.model.dao.TravelPlanDao;
import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.dto.PlanDetail;
import com.ssafy.trip.model.dto.TravelPlan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicTravelPlanService implements TravelPlanService {
    private final TravelPlanDao travelPlanDao;
    private final AttractionDao attractionDao;
    
    @Override
    @Transactional
    public int createPlan(TravelPlan plan) {
        log.debug("여행 계획 생성 요청: {}", plan.getTitle());
        
        // 계획 추가
        int result = travelPlanDao.insertPlan(plan);
        
        // 생성된 계획의 ID를 이용해 세부 일정 추가
        if (plan.getDetails() != null && !plan.getDetails().isEmpty()) {
            log.debug("세부 일정 {} 개 추가", plan.getDetails().size());
            for (PlanDetail detail : plan.getDetails()) {
                detail.setPlanId(plan.getPlanId());
                travelPlanDao.insertPlanDetail(detail);
            }
        }
        
        log.info("여행 계획 생성 완료. ID: {}", plan.getPlanId());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelPlan> getPlansByUserId(String userId) {
        log.debug("사용자 ID: {}의 여행 계획 목록 조회", userId);
        List<TravelPlan> plans = travelPlanDao.getPlansByUserId(userId);
        log.debug("조회된 여행 계획 수: {}", plans.size());
        return plans;
    }

    @Override
    @Transactional(readOnly = true)
    public TravelPlan getPlanWithDetails(int planId) {
        log.debug("여행 계획 상세 조회. 계획 ID: {}", planId);
        
        // 계획 기본 정보 조회
        TravelPlan plan = travelPlanDao.getPlanById(planId);
        if (plan == null) {
            log.warn("여행 계획을 찾을 수 없음. 계획 ID: {}", planId);
            throw new RecordNotFoundException("해당 ID의 여행 계획을 찾을 수 없습니다: " + planId);
        }
        
        // 세부 일정 조회
        List<PlanDetail> details = travelPlanDao.getPlanDetailsByPlanId(planId);
        plan.setDetails(details);
        
        // 관광지 정보 조회 및 연결
        if (details != null && !details.isEmpty()) {
            for (PlanDetail detail : details) {
                if (detail.getAttractionId() != null && detail.getAttractionId() > 0) {
                    try {
                        Attraction attraction = attractionDao.getAttractionByNo(detail.getAttractionId());
                        detail.setAttraction(attraction);
                    } catch (Exception e) {
                        log.warn("관광지 정보 조회 실패. 관광지 ID: {}, 오류: {}", detail.getAttractionId(), e.getMessage());
                    }
                }
            }
        }
        
        log.debug("여행 계획 조회 완료. 일정 수: {}", details != null ? details.size() : 0);
        return plan;
    }

 // BasicTravelPlanService.java의 updatePlan 메서드를 다음으로 수정

    @Override
    @Transactional
    public int updatePlan(TravelPlan plan) {
        log.debug("여행 계획 수정 요청. 계획 ID: {}", plan.getPlanId());
        
        // 기존 계획 존재 여부 확인
        TravelPlan existingPlan = travelPlanDao.getPlanById(plan.getPlanId());
        if (existingPlan == null) {
            log.warn("수정할 여행 계획을 찾을 수 없음. 계획 ID: {}", plan.getPlanId());
            throw new RecordNotFoundException("해당 ID의 여행 계획을 찾을 수 없습니다: " + plan.getPlanId());
        }
        
        // 계획 기본 정보 업데이트
        int result = travelPlanDao.updatePlan(plan);
        log.debug("계획 기본 정보 업데이트 완료");
        
        // 세부 일정 처리
        if (plan.getDetails() != null) {
            log.debug("새로운 세부 일정 {} 개 처리 시작", plan.getDetails().size());
            
            // 기존 세부 일정 조회
            List<PlanDetail> existingDetails = travelPlanDao.getPlanDetailsByPlanId(plan.getPlanId());
            log.debug("기존 세부 일정 {} 개 조회됨", existingDetails.size());
            
            // 새로운 세부 일정에서 detailId가 있는 것과 없는 것 분리
            List<PlanDetail> toUpdate = new ArrayList<>();
            List<PlanDetail> toInsert = new ArrayList<>();
            
            for (PlanDetail detail : plan.getDetails()) {
                detail.setPlanId(plan.getPlanId());
                
                if (detail.getDetailId() > 0) {
                    // 기존 일정 수정
                    toUpdate.add(detail);
                    log.debug("수정할 일정: detailId={}, title={}", detail.getDetailId(), detail.getTitle());
                } else {
                    // 새로운 일정 추가
                    toInsert.add(detail);
                    log.debug("추가할 일정: title={}", detail.getTitle());
                }
            }
            
            // 기존 일정 중 새로운 목록에 없는 것들은 삭제
            Set<Integer> newDetailIds = toUpdate.stream()
                .map(PlanDetail::getDetailId)
                .collect(Collectors.toSet());
                
            for (PlanDetail existingDetail : existingDetails) {
                if (!newDetailIds.contains(existingDetail.getDetailId())) {
                    log.debug("삭제할 일정: detailId={}, title={}", 
                        existingDetail.getDetailId(), existingDetail.getTitle());
                    travelPlanDao.deletePlanDetail(existingDetail.getDetailId());
                }
            }
            
            // 기존 일정 수정
            for (PlanDetail detail : toUpdate) {
                travelPlanDao.updatePlanDetail(detail);
                log.debug("일정 수정 완료: detailId={}", detail.getDetailId());
            }
            
            // 새로운 일정 추가
            for (PlanDetail detail : toInsert) {
                travelPlanDao.insertPlanDetail(detail);
                log.debug("일정 추가 완료: detailId={}", detail.getDetailId());
            }
            
            log.debug("세부 일정 처리 완료. 수정: {}개, 추가: {}개", toUpdate.size(), toInsert.size());
        }
        
        log.info("여행 계획 수정 완료. ID: {}", plan.getPlanId());
        return result;
    }
    
    @Override
    @Transactional
    public int deletePlan(int planId) {
        log.debug("여행 계획 삭제 요청. 계획 ID: {}", planId);
        
        // 기존 계획 존재 여부 확인
        TravelPlan existingPlan = travelPlanDao.getPlanById(planId);
        if (existingPlan == null) {
            log.warn("삭제할 여행 계획을 찾을 수 없음. 계획 ID: {}", planId);
            throw new RecordNotFoundException("해당 ID의 여행 계획을 찾을 수 없습니다: " + planId);
        }
        
        // 세부 일정 먼저 삭제 (참조 무결성)
        List<PlanDetail> details = travelPlanDao.getPlanDetailsByPlanId(planId);
        for (PlanDetail detail : details) {
            travelPlanDao.deletePlanDetail(detail.getDetailId());
        }
        
        // 계획 삭제
        int result = travelPlanDao.deletePlan(planId);
        
        log.info("여행 계획 삭제 완료. ID: {}", planId);
        return result;
    }
}