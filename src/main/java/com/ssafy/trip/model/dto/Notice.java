package com.ssafy.trip.model.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Notice {
    private int nno;
    private @NonNull String title;
    private @NonNull String content;
    private @NonNull String writer;
    private Timestamp regDate;
    private int viewCnt;
    private boolean isImportant; // 중요 공지사항 여부
    private int priority; // 우선순위 (높을수록 위에 표시)
    private LocalDate startDate; // 공지 시작일
    private LocalDate endDate; // 공지 종료일
    
    // 공지사항이 현재 유효한지 확인
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        return true;
    }
}