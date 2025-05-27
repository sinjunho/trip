package com.ssafy.trip.model.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardTagRelation {
    private int relationId;
    private int pboardNo;
    private int tagId;
    private Timestamp createdAt;
    
    // 생성자 - 연결 생성
    public PlanBoardTagRelation(int pboardNo, int tagId) {
        this.pboardNo = pboardNo;
        this.tagId = tagId;
    }
}