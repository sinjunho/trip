package com.ssafy.trip.model.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardComment {
    private int commentId;
    private int pboardNo;
    private String userId;
    private String writer;
    private String content;
    private Integer parentCommentId;
    private Timestamp regDate;
    private Timestamp updateDate;
    private boolean isDeleted;
    
    // 연관 데이터
    private List<PlanBoardComment> replies; // 대댓글 목록
    private int replyCount; // 대댓글 수
    
    // 생성자 - 기본 댓글
    public PlanBoardComment(int pboardNo, String userId, String writer, String content) {
        this.pboardNo = pboardNo;
        this.userId = userId;
        this.writer = writer;
        this.content = content;
        this.isDeleted = false;
    }
    
    // 생성자 - 대댓글
    public PlanBoardComment(int pboardNo, String userId, String writer, String content, int parentCommentId) {
        this(pboardNo, userId, writer, content);
        this.parentCommentId = parentCommentId;
    }
}