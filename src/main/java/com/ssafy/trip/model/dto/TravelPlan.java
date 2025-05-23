package com.ssafy.trip.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlan {
    private int planId;
    private String userId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublic; // 공개 여부 필드 추가
    private int viewCount; // 조회수 필드 추가
    private List<PlanDetail> details;
    
    // 공개 여부 확인 메서드
    public boolean isPublic() {
        return this.isPublic;
    }
    
    // 생성자 (공개 여부 기본값 false)
    public TravelPlan(String userId, String title, String description, 
                     LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPublic = false; // 기본값은 비공개
        this.viewCount = 0;
    }
}