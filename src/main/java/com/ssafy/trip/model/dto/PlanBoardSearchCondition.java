package com.ssafy.trip.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardSearchCondition extends SearchCondition {
    // 기본 검색 조건 (부모 클래스에서 상속)
    // private String key;
    // private String word;
    // private int currentPage;
    // private int itemsPerPage;
    
    // 현재 사용자 ID (좋아요 표시 여부 확인용)
    private String userId;
    
    // 여행 게시판 전용 검색 조건
    private String travelTheme; // 여행 테마 필터
    private String destination; // 목적지 필터
    private LocalDate startDateFrom; // 시작일 범위 (이후)
    private LocalDate startDateTo; // 시작일 범위 (이전)
    private Integer budgetMin; // 최소 예산
    private Integer budgetMax; // 최대 예산
    private Integer participantCount; // 인원수
    private String sortBy; // 정렬 기준 (latest, popular, likes)
    private boolean onlyFeatured; // 추천 게시글만 보기
    private String tagName; // 태그별 검색
    
    // 생성자 - 기본 검색
    public PlanBoardSearchCondition(String key, String word, int currentPage) {
        super(key, word, currentPage);
        this.sortBy = "latest"; // 기본값: 최신순
    }
    
    // 생성자 - 테마별 검색
    public PlanBoardSearchCondition(String travelTheme, int currentPage) {
        super(null, null, currentPage);
        this.travelTheme = travelTheme;
        this.sortBy = "latest";
    }
}