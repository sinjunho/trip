package com.ssafy.trip.model.dto;

import java.sql.Timestamp;

public class Board {
    private int bno;
    private String title;
    private String content;
    private String writer;
    private Timestamp  regDate;
    private int viewCnt;

    public Board() {
        super();
    }
    
    public Board(String title, String content, String writer) {
        this(0, title, content, writer, null, 0);
    }

    public Board(int bno, String title, String content, String writer, Timestamp regDate, int viewCnt) {
        this.bno = bno;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.regDate = regDate;
        this.viewCnt = viewCnt;
    }

    public int getBno() {
        return bno;
    }

    public void setBno(int bno) {
        this.bno = bno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Timestamp getRegDate() {
        return regDate;
    }

    public void setRegDate(Timestamp regDate) {
        this.regDate = regDate;
    }

    public int getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(int viewCnt) {
        this.viewCnt = viewCnt;
    }

    @Override
    public String toString() {
        return "Board [bno=" + bno + ", title=" + title + ", content=" + content + ", writer=" + writer + ", regDate=" + regDate + ", viewCnt=" + viewCnt + "]";
    }
}
