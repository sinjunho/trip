package com.ssafy.trip.model.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int commentId;
    private int contentId;        // bno 대신 contentId 사용
    private String contentType;   // 'board', 'attraction', 'plan' 등
    private String content;
    private String writer;
    private Timestamp regDate;
    private Integer parentId;
    private int depth;
    
    // 기존 호환성을 위한 getter/setter
    public int getBno() {
        return this.contentId;
    }
    
    public void setBno(int bno) {
        this.contentId = bno;
        if (this.contentType == null) {
            this.contentType = "board";
        }
    }
}