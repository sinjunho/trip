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
@RequiredArgsConstructor
public class Board {
    private int bno;
    private @NonNull String title;
    private @NonNull String content;
    private @NonNull String writer;
    private Timestamp  regDate;
    private int viewCnt;


}
