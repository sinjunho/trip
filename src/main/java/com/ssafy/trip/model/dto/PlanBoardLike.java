package com.ssafy.trip.model.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardLike {
    private int likeId;
    private int pboardNo;
    private String userId;
    private Timestamp createdAt;
    
    // 생성자 - 좋아요 생성
    public PlanBoardLike(int pboardNo, String userId) {
        this.pboardNo = pboardNo;
        this.userId = userId;
    }
}
