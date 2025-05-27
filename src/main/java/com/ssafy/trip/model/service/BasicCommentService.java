// BasicCommentService.java
package com.ssafy.trip.model.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ssafy.trip.model.dao.CommentDao;
import com.ssafy.trip.model.dto.Comment;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicCommentService implements CommentService {
    
    private final CommentDao commentDao;
    
    @Override
    public int createComment(Comment comment) {
        // 기본값 설정
        if (comment.getContentType() == null) {
            comment.setContentType("board");
        }
        
        // 대댓글인 경우 depth 설정
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            comment.setDepth(1);
        } else {
            comment.setDepth(0);
            comment.setParentId(null);
        }
        
        return commentDao.insert(comment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBoard(int boardId) {
        return commentDao.getCommentsByBoard(boardId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(int commentId) {
        return commentDao.selectById(commentId);
    }
    
    @Override
    public int updateComment(Comment comment) {
        return commentDao.update(comment);
    }
    
    @Override
    public int deleteComment(int commentId) {
        // 자식 댓글이 있는지 확인
        List<Comment> childComments = commentDao.getChildComments(commentId);
        
        // 자식 댓글부터 삭제
        for (Comment child : childComments) {
            commentDao.delete(child.getCommentId());
        }
        
        // 부모 댓글 삭제
        return commentDao.delete(commentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getCommentCount(int contentId, String contentType) {
        return commentDao.getCommentCount(contentId, contentType);
    }
}