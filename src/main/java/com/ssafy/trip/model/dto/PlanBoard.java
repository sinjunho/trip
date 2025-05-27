package com.ssafy.trip.model.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoard {
    // 기본 게시판 정보
    private int pboardNo;
    private String title;
    private String content;
    private String writer;
    private String userId;
    private Timestamp regDate;
    private Timestamp updateDate;
    private int viewCnt;
    
    // 여행 계획 연결 정보
    private Integer planId;
    
    // 여행 정보 요약
    private String travelTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String travelDestinations;
    private String travelTheme;
    private Integer estimatedBudget;
    private int participantCount;
    private Integer travelDuration;
    
    // 게시판 기능

    private boolean isFeatured;
    private int likeCount;
    private int commentCount;
    
    // 첨부 파일
    private String thumbnailImage;
    private String attachmentFiles;
    
    // 연관 데이터 (조회시 사용)
    private List<PlanBoardComment> comments;
    private List<PlanBoardTag> tags;
    private boolean isLiked; // 현재 사용자가 좋아요 했는지 여부
    
    // 생성자 - 기본 정보만
    public PlanBoard(String title, String content, String writer, String userId) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.userId = userId;
        this.isPublic = true;
        this.participantCount = 1;
    }
    
    // 생성자 - 여행 정보 포함
    public PlanBoard(String title, String content, String writer, String userId,
                    String travelTitle, LocalDate startDate, LocalDate endDate,
                    String travelTheme, int participantCount) {
        this(title, content, writer, userId);
        this.travelTitle = travelTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travelTheme = travelTheme;
        this.participantCount = participantCount;
        
        // 여행 일수 자동 계산
        if (startDate != null && endDate != null) {
            this.travelDuration = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }
    
    @JsonProperty("isPublic")
    private Boolean isPublic; // Boolean 타입으로 변경하여 null 처리 가능

    // 또는 커스텀 setter 추가
    public void setIsPublic(Object isPublic) {
        if (isPublic instanceof Boolean) {
            this.isPublic = (Boolean) isPublic;
        } else if (isPublic instanceof String) {
            this.isPublic = "true".equalsIgnoreCase((String) isPublic) || "1".equals(isPublic);
        } else if (isPublic instanceof Number) {
            this.isPublic = ((Number) isPublic).intValue() == 1;
        } else {
            this.isPublic = true; // 기본값
        }
    }
}