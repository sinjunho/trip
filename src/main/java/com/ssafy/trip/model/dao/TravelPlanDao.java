package com.ssafy.trip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.trip.model.dto.PlanDetail;
import com.ssafy.trip.model.dto.TravelPlan;

@Mapper
public interface TravelPlanDao {
    int insertPlan(TravelPlan plan);
    int insertPlanDetail(PlanDetail detail);
    List<TravelPlan> getPlansByUserId(String userId);
    TravelPlan getPlanById(int planId);
    List<PlanDetail> getPlanDetailsByPlanId(int planId);
    int updatePlan(TravelPlan plan);
    int updatePlanDetail(PlanDetail detail);
    int deletePlan(int planId);
    int deletePlanDetail(int detailId);
}