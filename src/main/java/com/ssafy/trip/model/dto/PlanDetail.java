package com.ssafy.trip.model.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetail {
    private int detailId;
    private int planId;
    private int dayNumber;
    private Integer attractionId;
    private String title;
    private String description;
    private LocalTime visitTime;
    private Integer stayDuration; // 분 단위
    private int orderNo;
    private Attraction attraction; // 연관된 관광지 정보
}