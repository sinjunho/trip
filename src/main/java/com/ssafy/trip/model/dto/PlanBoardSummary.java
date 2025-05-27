package com.ssafy.trip.model.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시판 목록 조회시 사용하는 요약 정보 DTO
 * 상세 정보가 필요 없는 목록 조회 성능 최적화용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardSummary {
    private int pboardNo;
    private String title;
    private String writer;
    private String userId;
    private Timestamp regDate;
    private int viewCnt;
    private int likeCount;
    private int commentCount;
    
    // 여행 정보 요약
    private String travelTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String travelDestinations;
    private String travelTheme;
    private Integer estimatedBudget;
    private int participantCount;
    private Integer travelDuration;
    
    // 표시용 정보
    private String thumbnailImage;
    private boolean isFeatured;
    private boolean isLiked; // 현재 사용자의 좋아요 여부
    
    // 태그 목록 (문자열로 연결)
    private String tagNames; // "제주도,힐링,혼자여행" 형태
}