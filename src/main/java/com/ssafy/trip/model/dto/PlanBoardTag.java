package com.ssafy.trip.model.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanBoardTag {
    private int tagId;
    private String tagName;
    private int useCount;
    private Timestamp createdAt;
    
    // 생성자 - 태그명만
    public PlanBoardTag(String tagName) {
        this.tagName = tagName;
        this.useCount = 0;
    }
}