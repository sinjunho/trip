package com.ssafy.trip.model.service;

import java.util.List;

import com.ssafy.trip.model.dto.TravelPlan;

public interface TravelPlanService {
    int createPlan(TravelPlan plan);
    List<TravelPlan> getPlansByUserId(String userId);
    TravelPlan getPlanWithDetails(int planId);
    int updatePlan(TravelPlan plan);
    int deletePlan(int planId);
}