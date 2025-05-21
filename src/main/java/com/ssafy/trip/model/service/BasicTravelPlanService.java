package com.ssafy.trip.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.trip.model.dao.AttractionDao;
import com.ssafy.trip.model.dao.TravelPlanDao;
import com.ssafy.trip.model.dto.PlanDetail;
import com.ssafy.trip.model.dto.TravelPlan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicTravelPlanService implements TravelPlanService {
    private final TravelPlanDao travelPlanDao;
    private final AttractionDao attractionDao;
    
    @Override
    @Transactional
    public int createPlan(TravelPlan plan) {
        // 계획 추가
        int result = travelPlanDao.insertPlan(plan);
        
        // 생성된 계획의 ID를 이용해 세부 일정 추가
        if (plan.getDetails() != null) {
            for (PlanDetail detail : plan.getDetails()) {
                detail.setPlanId(plan.getPlanId());
                travelPlanDao.insertPlanDetail(detail);
            }
        }
        
        return result;
    }

	@Override
	public List<TravelPlan> getPlansByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TravelPlan getPlanWithDetails(int planId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updatePlan(TravelPlan plan) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deletePlan(int planId) {
		// TODO Auto-generated method stub
		return 0;
	}
    
    // 다른 메서드 구현
}