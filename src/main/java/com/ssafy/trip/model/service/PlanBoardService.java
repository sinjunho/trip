package com.ssafy.trip.model.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dto.PlanBoard;
import com.ssafy.trip.model.dto.PlanBoardComment;
import com.ssafy.trip.model.dto.PlanBoardLike;
import com.ssafy.trip.model.dto.PlanBoardSearchCondition;
import com.ssafy.trip.model.dto.PlanBoardSummary;
import com.ssafy.trip.model.dto.PlanBoardTag;

public interface PlanBoardService {
    
    // ======================== 게시글 CRUD ========================
    
    /**
     * 게시글 등록
     */
    int createPlanBoard(PlanBoard planBoard) throws Exception;
    
    /**
     * 게시글 목록 조회 (요약 정보)
     */
    List<PlanBoardSummary> getPlanBoardList(PlanBoardSearchCondition condition) throws Exception;
    
    /**
     * 게시글 상세 조회
     */
    PlanBoard getPlanBoardDetail(int pboardNo, String userId) throws Exception;
    
    /**
     * 게시글 수정
     */
    int updatePlanBoard(PlanBoard planBoard) throws Exception;
    
    /**
     * 게시글 삭제
     */
    int deletePlanBoard(int pboardNo) throws Exception;
    
    /**
     * 조회수 증가
     */
    void increaseViewCount(int pboardNo) throws Exception;
    
    /**
     * 전체 게시글 수 조회
     */
    int getTotalPlanBoardCount(PlanBoardSearchCondition condition) throws Exception;
    
    /**
     * 추천 게시글 목록 조회
     */
    List<PlanBoardSummary> getFeaturedPlanBoards(int limit) throws Exception;
    
    /**
     * 인기 게시글 목록 조회 (좋아요 순)
     */
    List<PlanBoardSummary> getPopularPlanBoards(int limit) throws Exception;
    
    /**
     * 사용자별 게시글 목록 조회
     */
    List<PlanBoardSummary> getPlanBoardsByUserId(String userId, int offset, int limit) throws Exception;
    
    // ======================== 댓글 CRUD ========================
    
    /**
     * 댓글 등록
     */
    int createComment(PlanBoardComment comment) throws Exception;
    
    /**
     * 게시글별 댓글 목록 조회
     */
    List<PlanBoardComment> getCommentsByPboardNo(int pboardNo) throws Exception;
    
    /**
     * 댓글 수정
     */
    int updateComment(PlanBoardComment comment) throws Exception;
    
    /**
     * 댓글 삭제
     */
    int deleteComment(int commentId) throws Exception;
    
    /**
     * 대댓글 목록 조회
     */
    List<PlanBoardComment> getRepliesByParentId(int parentCommentId) throws Exception;
    
    // ======================== 좋아요 관리 ========================
    
    /**
     * 좋아요 추가/제거 토글
     */
    boolean toggleLike(int pboardNo, String userId) throws Exception;
    
    /**
     * 좋아요 여부 확인
     */
    boolean isLikedByUser(int pboardNo, String userId) throws Exception;
    
    /**
     * 게시글별 좋아요 목록 조회
     */
    List<PlanBoardLike> getLikesByPboardNo(int pboardNo) throws Exception;
    
    // ======================== 태그 관리 ========================
    
    /**
     * 태그 추가 (없으면 생성)
     */
    int addTag(String tagName) throws Exception;
    
    /**
     * 게시글에 태그 추가
     */
    void addTagToPlanBoard(int pboardNo, String tagName) throws Exception;
    
    /**
     * 게시글에서 태그 제거
     */
    void removeTagFromPlanBoard(int pboardNo, int tagId) throws Exception;
    
    /**
     * 게시글의 모든 태그 제거
     */
    void removeAllTagsFromPlanBoard(int pboardNo) throws Exception;
    
    /**
     * 게시글의 태그 목록 조회
     */
    List<PlanBoardTag> getTagsByPboardNo(int pboardNo) throws Exception;
    
    /**
     * 인기 태그 목록 조회
     */
    List<PlanBoardTag> getPopularTags(int limit) throws Exception;
    
    /**
     * 태그별 게시글 목록 조회
     */
    List<PlanBoardSummary> getPlanBoardsByTag(String tagName, int offset, int limit) throws Exception;
    
    // ======================== 통계 및 기타 ========================
    
    /**
     * 여행 테마별 통계
     */
    List<Map<String, Object>> getTravelThemeStatistics() throws Exception;
    
    /**
     * 월별 게시글 통계
     */
    List<Map<String, Object>> getMonthlyStatistics(int year) throws Exception;
    
    /**
     * 게시글 검색
     */
    List<PlanBoardSummary> searchPlanBoards(String keyword, String searchType, int offset, int limit) throws Exception;
    
    /**
     * 검색 결과 수 조회
     */
    int getSearchResultCount(String keyword, String searchType) throws Exception;
}