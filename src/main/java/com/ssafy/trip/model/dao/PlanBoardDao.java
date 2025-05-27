package com.ssafy.trip.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.trip.model.dto.PlanBoard;
import com.ssafy.trip.model.dto.PlanBoardComment;
import com.ssafy.trip.model.dto.PlanBoardLike;
import com.ssafy.trip.model.dto.PlanBoardSearchCondition;
import com.ssafy.trip.model.dto.PlanBoardSummary;
import com.ssafy.trip.model.dto.PlanBoardTag;
import com.ssafy.trip.model.dto.PlanBoardTagRelation;

@Mapper
public interface PlanBoardDao {
    
    // ======================== 게시글 CRUD ========================
    
    /**
     * 게시글 등록
     */
    int insertPlanBoard(PlanBoard planBoard) throws SQLException;
    
    /**
     * 게시글 목록 조회 (요약 정보)
     */
    List<PlanBoardSummary> selectPlanBoardList(PlanBoardSearchCondition condition) throws SQLException;
    
    /**
     * 게시글 상세 조회
     */
    PlanBoard selectPlanBoardDetail(@Param("pboardNo") int pboardNo) throws SQLException;
    
    /**
     * 게시글 수정
     */
    int updatePlanBoard(PlanBoard planBoard) throws SQLException;
    
    /**
     * 게시글 삭제
     */
    int deletePlanBoard(@Param("pboardNo") int pboardNo) throws SQLException;
    
    /**
     * 조회수 증가
     */
    int updateViewCount(@Param("pboardNo") int pboardNo) throws SQLException;
    
    /**
     * 전체 게시글 수 조회
     */
    int getTotalPlanBoardCount(PlanBoardSearchCondition condition) throws SQLException;
    
    /**
     * 추천 게시글 목록 조회
     */
    List<PlanBoardSummary> selectFeaturedPlanBoards(@Param("limit") int limit) throws SQLException;
    
    /**
     * 인기 게시글 목록 조회 (좋아요 순)
     */
    List<PlanBoardSummary> selectPopularPlanBoards(@Param("limit") int limit) throws SQLException;
    
    /**
     * 사용자별 게시글 목록 조회
     */
    List<PlanBoardSummary> selectPlanBoardsByUserId(@Param("userId") String userId, 
                                                   @Param("offset") int offset, 
                                                   @Param("limit") int limit) throws SQLException;
    
    // ======================== 댓글 CRUD ========================
    
    /**
     * 댓글 등록
     */
    int insertComment(PlanBoardComment comment) throws SQLException;
    
    /**
     * 게시글별 댓글 목록 조회
     */
    List<PlanBoardComment> selectCommentsByPboardNo(@Param("pboardNo") int pboardNo) throws SQLException;
    
    /**
     * 댓글 수정
     */
    int updateComment(PlanBoardComment comment) throws SQLException;
    
    /**
     * 댓글 삭제 (soft delete)
     */
    int deleteComment(@Param("commentId") int commentId) throws SQLException;
    
    /**
     * 대댓글 목록 조회
     */
    List<PlanBoardComment> selectRepliesByParentId(@Param("parentCommentId") int parentCommentId) throws SQLException;
    
    // ======================== 좋아요 관리 ========================
    
    /**
     * 좋아요 추가
     */
    int insertLike(PlanBoardLike like) throws SQLException;
    
    /**
     * 좋아요 삭제
     */
    int deleteLike(@Param("pboardNo") int pboardNo, @Param("userId") String userId) throws SQLException;
    
    /**
     * 좋아요 여부 확인
     */
    boolean isLikedByUser(@Param("pboardNo") int pboardNo, @Param("userId") String userId) throws SQLException;
    
    /**
     * 게시글별 좋아요 목록 조회
     */
    List<PlanBoardLike> selectLikesByPboardNo(@Param("pboardNo") int pboardNo) throws SQLException;
    
    // ======================== 태그 관리 ========================
    
    /**
     * 태그 등록 (없으면 생성)
     */
    int insertTag(PlanBoardTag tag) throws SQLException;
    
    /**
     * 태그명으로 태그 조회
     */
    PlanBoardTag selectTagByName(@Param("tagName") String tagName) throws SQLException;
    
    /**
     * 인기 태그 목록 조회
     */
    List<PlanBoardTag> selectPopularTags(@Param("limit") int limit) throws SQLException;
    
    /**
     * 게시글-태그 연결
     */
    int insertTagRelation(PlanBoardTagRelation relation) throws SQLException;
    
    /**
     * 게시글-태그 연결 해제
     */
    int deleteTagRelation(@Param("pboardNo") int pboardNo, @Param("tagId") int tagId) throws SQLException;
    
    /**
     * 게시글의 모든 태그 연결 해제
     */
    int deleteAllTagRelations(@Param("pboardNo") int pboardNo) throws SQLException;
    
    /**
     * 게시글별 태그 목록 조회
     */
    List<PlanBoardTag> selectTagsByPboardNo(@Param("pboardNo") int pboardNo) throws SQLException;
    
    /**
     * 태그별 게시글 목록 조회
     */
    List<PlanBoardSummary> selectPlanBoardsByTag(@Param("tagName") String tagName, 
                                                @Param("offset") int offset, 
                                                @Param("limit") int limit) throws SQLException;
    
    // ======================== 통계 및 기타 ========================
    
    /**
     * 여행 테마별 통계
     */
    List<java.util.Map<String, Object>> selectTravelThemeStatistics() throws SQLException;
    
    /**
     * 월별 게시글 통계
     */
    List<java.util.Map<String, Object>> selectMonthlyStatistics(@Param("year") int year) throws SQLException;
    
    /**
     * 전체 게시글 수 조회
     */
    int getTotalPlanBoardCountAll() throws SQLException;
    
    /**
     * 게시글 검색 (제목, 내용, 작성자)
     */
    List<PlanBoardSummary> searchPlanBoards(@Param("keyword") String keyword, 
                                           @Param("searchType") String searchType,
                                           @Param("offset") int offset, 
                                           @Param("limit") int limit) throws SQLException;
    
    /**
     * 검색 결과 수 조회
     */
    int getSearchResultCount(@Param("keyword") String keyword, 
                            @Param("searchType") String searchType) throws SQLException;
}