package com.ssafy.trip.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ssafy.trip.model.dto.Comment;

@Mapper
public interface CommentDao {
    
    // 댓글 등록
    int insert(Comment comment);
    
    // 게시글별 댓글 조회
    List<Comment> getCommentsByBoard(@Param("contentId") int contentId);
    
    // 댓글 ID로 조회
    Comment selectById(@Param("commentId") int commentId);
    
    // 댓글 수정
    int update(Comment comment);
    
    // 댓글 삭제
    int delete(@Param("commentId") int commentId);
    
    // 자식 댓글 조회
    List<Comment> getChildComments(@Param("parentId") int parentId);
    
    // 댓글 수 조회
    int getCommentCount(@Param("contentId") int contentId, @Param("contentType") String contentType);
}