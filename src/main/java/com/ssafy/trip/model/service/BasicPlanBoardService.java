package com.ssafy.trip.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.trip.model.dao.PlanBoardDao;
import com.ssafy.trip.model.dto.PlanBoard;
import com.ssafy.trip.model.dto.PlanBoardComment;
import com.ssafy.trip.model.dto.PlanBoardLike;
import com.ssafy.trip.model.dto.PlanBoardSearchCondition;
import com.ssafy.trip.model.dto.PlanBoardSummary;
import com.ssafy.trip.model.dto.PlanBoardTag;
import com.ssafy.trip.model.dto.PlanBoardTagRelation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicPlanBoardService implements PlanBoardService {

    private final PlanBoardDao planBoardDao;
    
    // ======================== 게시글 CRUD ========================
    
    @Override
    @Transactional
    public int createPlanBoard(PlanBoard planBoard) throws Exception {
        log.debug("게시글 등록 서비스 호출: {}", planBoard);
        
        // 여행 일수 자동 계산 (입력되지 않은 경우)
        if (planBoard.getTravelDuration() == null && planBoard.getStartDate() != null && planBoard.getEndDate() != null) {
            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(planBoard.getStartDate(), planBoard.getEndDate()) + 1;
            planBoard.setTravelDuration(days);
        }
        
        // 게시글 등록
        int result = planBoardDao.insertPlanBoard(planBoard);
        
        // 태그 처리 (태그가 있는 경우)
        if (planBoard.getTags() != null && !planBoard.getTags().isEmpty()) {
            for (PlanBoardTag tag : planBoard.getTags()) {
                addTagToPlanBoard(planBoard.getPboardNo(), tag.getTagName());
            }
        }
        
        return result;
    }

    @Override
    public List<PlanBoardSummary> getPlanBoardList(PlanBoardSearchCondition condition) throws Exception {
        log.debug("게시글 목록 조회 서비스 호출: {}", condition);
        return planBoardDao.selectPlanBoardList(condition);
    }

    @Override
    @Transactional
    public PlanBoard getPlanBoardDetail(int pboardNo, String userId) throws Exception {
        log.debug("게시글 상세 조회 서비스 호출: pboardNo={}, userId={}", pboardNo, userId);
        
        // 게시글 조회
        PlanBoard planBoard = planBoardDao.selectPlanBoardDetail(pboardNo);
        
        if (planBoard != null) {
            // 댓글 목록 조회
            List<PlanBoardComment> comments = getCommentsByPboardNo(pboardNo);
            planBoard.setComments(comments);
            
            // 태그 목록 조회
            List<PlanBoardTag> tags = getTagsByPboardNo(pboardNo);
            planBoard.setTags(tags);
            
            // 현재 사용자의 좋아요 여부 확인
            if (userId != null) {
                planBoard.setLiked(isLikedByUser(pboardNo, userId));
            }
        }
        
        return planBoard;
    }

    @Override
    @Transactional
    public int updatePlanBoard(PlanBoard planBoard) throws Exception {
        log.debug("게시글 수정 서비스 호출: {}", planBoard);
        
        // 여행 일수 자동 계산 (입력되지 않은 경우)
        if (planBoard.getTravelDuration() == null && planBoard.getStartDate() != null && planBoard.getEndDate() != null) {
            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(planBoard.getStartDate(), planBoard.getEndDate()) + 1;
            planBoard.setTravelDuration(days);
        }
        
        // 게시글 수정
        int result = planBoardDao.updatePlanBoard(planBoard);
        
        // 태그 업데이트 (기존 태그 모두 제거 후 새로 추가)
        if (planBoard.getTags() != null) {
            removeAllTagsFromPlanBoard(planBoard.getPboardNo());
            
            for (PlanBoardTag tag : planBoard.getTags()) {
                addTagToPlanBoard(planBoard.getPboardNo(), tag.getTagName());
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public int deletePlanBoard(int pboardNo) throws Exception {
        log.debug("게시글 삭제 서비스 호출: pboardNo={}", pboardNo);
        
        // 연관 데이터 정리 (태그 관계, 좋아요 등은 CASCADE로 처리 가능)
        // 태그 관계 삭제
        removeAllTagsFromPlanBoard(pboardNo);
        
        return planBoardDao.deletePlanBoard(pboardNo);
    }

    @Override
    public void increaseViewCount(int pboardNo) throws Exception {
        log.debug("조회수 증가 서비스 호출: pboardNo={}", pboardNo);
        planBoardDao.updateViewCount(pboardNo);
    }

    @Override
    public int getTotalPlanBoardCount(PlanBoardSearchCondition condition) throws Exception {
        log.debug("전체 게시글 수 조회 서비스 호출: {}", condition);
        return planBoardDao.getTotalPlanBoardCount(condition);
    }

    @Override
    public List<PlanBoardSummary> getFeaturedPlanBoards(int limit) throws Exception {
        log.debug("추천 게시글 목록 조회 서비스 호출: limit={}", limit);
        return planBoardDao.selectFeaturedPlanBoards(limit);
    }

    @Override
    public List<PlanBoardSummary> getPopularPlanBoards(int limit) throws Exception {
        log.debug("인기 게시글 목록 조회 서비스 호출: limit={}", limit);
        return planBoardDao.selectPopularPlanBoards(limit);
    }

    @Override
    public List<PlanBoardSummary> getPlanBoardsByUserId(String userId, int offset, int limit) throws Exception {
        log.debug("사용자별 게시글 목록 조회 서비스 호출: userId={}, offset={}, limit={}", userId, offset, limit);
        return planBoardDao.selectPlanBoardsByUserId(userId, offset, limit);
    }
    
    // ======================== 댓글 CRUD ========================
    
    @Override
    @Transactional
    public int createComment(PlanBoardComment comment) throws Exception {
        log.debug("댓글 등록 서비스 호출: {}", comment);
        
        // 댓글 등록
        int result = planBoardDao.insertComment(comment);
        
        // 댓글 수 증가 로직 추가 필요 (plan_board 테이블의 comment_count 업데이트)
        // 향후 추가 구현
        
        return result;
    }

    @Override
    public List<PlanBoardComment> getCommentsByPboardNo(int pboardNo) throws Exception {
        log.debug("게시글별 댓글 목록 조회 서비스 호출: pboardNo={}", pboardNo);
        
        // 댓글 목록 조회 (최상위 댓글만)
        List<PlanBoardComment> comments = planBoardDao.selectCommentsByPboardNo(pboardNo);
        
        // 각 댓글의 대댓글 조회 및 설정
        if (comments != null && !comments.isEmpty()) {
            for (PlanBoardComment comment : comments) {
                // 대댓글이 있는 경우만 조회
                if (comment.getReplyCount() > 0) {
                    List<PlanBoardComment> replies = planBoardDao.selectRepliesByParentId(comment.getCommentId());
                    comment.setReplies(replies);
                } else {
                    comment.setReplies(new ArrayList<>());
                }
            }
        }
        
        return comments;
    }

    @Override
    public int updateComment(PlanBoardComment comment) throws Exception {
        log.debug("댓글 수정 서비스 호출: {}", comment);
        return planBoardDao.updateComment(comment);
    }

    @Override
    public int deleteComment(int commentId) throws Exception {
        log.debug("댓글 삭제 서비스 호출: commentId={}", commentId);
        
        // 소프트 딜리트 (is_deleted = true로 설정)
        int result = planBoardDao.deleteComment(commentId);
        
        // 댓글 수 감소 로직 추가 필요 (plan_board 테이블의 comment_count 업데이트)
        // 향후 추가 구현
        
        return result;
    }

    @Override
    public List<PlanBoardComment> getRepliesByParentId(int parentCommentId) throws Exception {
        log.debug("대댓글 목록 조회 서비스 호출: parentCommentId={}", parentCommentId);
        return planBoardDao.selectRepliesByParentId(parentCommentId);
    }
    
    // ======================== 좋아요 관리 ========================
    
    @Override
    @Transactional
    public boolean toggleLike(int pboardNo, String userId) throws Exception {
        log.debug("좋아요 토글 서비스 호출: pboardNo={}, userId={}", pboardNo, userId);
        
        boolean isLiked = isLikedByUser(pboardNo, userId);
        
        if (isLiked) {
            // 이미 좋아요한 경우 -> 좋아요 취소
            planBoardDao.deleteLike(pboardNo, userId);
            // 좋아요 수 감소 로직 추가 필요 (plan_board 테이블의 like_count 업데이트)
            return false;
        } else {
            // 좋아요하지 않은 경우 -> 좋아요 추가
            PlanBoardLike like = new PlanBoardLike(pboardNo, userId);
            planBoardDao.insertLike(like);
            // 좋아요 수 증가 로직 추가 필요 (plan_board 테이블의 like_count 업데이트)
            return true;
        }
    }

    @Override
    public boolean isLikedByUser(int pboardNo, String userId) throws Exception {
        log.debug("좋아요 여부 확인 서비스 호출: pboardNo={}, userId={}", pboardNo, userId);
        
        if (userId == null) {
            return false;
        }
        
        return planBoardDao.isLikedByUser(pboardNo, userId);
    }

    @Override
    public List<PlanBoardLike> getLikesByPboardNo(int pboardNo) throws Exception {
        log.debug("게시글별 좋아요 목록 조회 서비스 호출: pboardNo={}", pboardNo);
        return planBoardDao.selectLikesByPboardNo(pboardNo);
    }
    
    // ======================== 태그 관리 ========================
    
    @Override
    @Transactional
    public int addTag(String tagName) throws Exception {
        log.debug("태그 추가 서비스 호출: tagName={}", tagName);
        
        // 이미 존재하는 태그인지 확인
        PlanBoardTag existingTag = planBoardDao.selectTagByName(tagName);
        
        if (existingTag != null) {
            // 이미 존재하면 ID 반환
            return existingTag.getTagId();
        } else {
            // 없으면 새로 생성
            PlanBoardTag newTag = new PlanBoardTag(tagName);
            planBoardDao.insertTag(newTag);
            return newTag.getTagId();
        }
    }

    @Override
    @Transactional
    public void addTagToPlanBoard(int pboardNo, String tagName) throws Exception {
        log.debug("게시글에 태그 추가 서비스 호출: pboardNo={}, tagName={}", pboardNo, tagName);
        
        // 태그 생성 또는 조회
        int tagId = addTag(tagName);
        
        // 게시글-태그 연결
        PlanBoardTagRelation relation = new PlanBoardTagRelation(pboardNo, tagId);
        planBoardDao.insertTagRelation(relation);
    }

    @Override
    public void removeTagFromPlanBoard(int pboardNo, int tagId) throws Exception {
        log.debug("게시글에서 태그 제거 서비스 호출: pboardNo={}, tagId={}", pboardNo, tagId);
        planBoardDao.deleteTagRelation(pboardNo, tagId);
    }

    @Override
    public void removeAllTagsFromPlanBoard(int pboardNo) throws Exception {
        log.debug("게시글의 모든 태그 제거 서비스 호출: pboardNo={}", pboardNo);
        planBoardDao.deleteAllTagRelations(pboardNo);
    }

    @Override
    public List<PlanBoardTag> getTagsByPboardNo(int pboardNo) throws Exception {
        log.debug("게시글의 태그 목록 조회 서비스 호출: pboardNo={}", pboardNo);
        return planBoardDao.selectTagsByPboardNo(pboardNo);
    }

    @Override
    public List<PlanBoardTag> getPopularTags(int limit) throws Exception {
        log.debug("인기 태그 목록 조회 서비스 호출: limit={}", limit);
        return planBoardDao.selectPopularTags(limit);
    }

    @Override
    public List<PlanBoardSummary> getPlanBoardsByTag(String tagName, int offset, int limit) throws Exception {
        log.debug("태그별 게시글 목록 조회 서비스 호출: tagName={}, offset={}, limit={}", tagName, offset, limit);
        return planBoardDao.selectPlanBoardsByTag(tagName, offset, limit);
    }
    
    // ======================== 통계 및 기타 ========================
    
    @Override
    public List<Map<String, Object>> getTravelThemeStatistics() throws Exception {
        log.debug("여행 테마별 통계 조회 서비스 호출");
        return planBoardDao.selectTravelThemeStatistics();
    }

    @Override
    public List<Map<String, Object>> getMonthlyStatistics(int year) throws Exception {
        log.debug("월별 게시글 통계 조회 서비스 호출: year={}", year);
        return planBoardDao.selectMonthlyStatistics(year);
    }

    @Override
    public List<PlanBoardSummary> searchPlanBoards(String keyword, String searchType, int offset, int limit) throws Exception {
        log.debug("게시글 검색 서비스 호출: keyword={}, searchType={}, offset={}, limit={}", keyword, searchType, offset, limit);
        return planBoardDao.searchPlanBoards(keyword, searchType, offset, limit);
    }

    @Override
    public int getSearchResultCount(String keyword, String searchType) throws Exception {
        log.debug("검색 결과 수 조회 서비스 호출: keyword={}, searchType={}", keyword, searchType);
        return planBoardDao.getSearchResultCount(keyword, searchType);
    }
}