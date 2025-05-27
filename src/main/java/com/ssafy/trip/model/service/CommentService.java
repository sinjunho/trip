
// CommentService.java
package com.ssafy.trip.model.service;

import java.util.List;
import com.ssafy.trip.model.dto.Comment;

public interface CommentService {
    
    // 댓글 등록
    int createComment(Comment comment);
    
    // 게시글별 댓글 조회
    List<Comment> getCommentsByBoard(int boardId);
    
    // 댓글 ID로 조회
    Comment getCommentById(int commentId);
    
    // 댓글 수정
    int updateComment(Comment comment);
    
    // 댓글 삭제
    int deleteComment(int commentId);
    
    // 댓글 수 조회
    int getCommentCount(int contentId, String contentType);
}