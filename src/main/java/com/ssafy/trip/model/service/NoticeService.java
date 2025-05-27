package com.ssafy.trip.model.service;

import java.sql.SQLException;

import com.ssafy.trip.model.dto.Notice;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

public interface NoticeService {
    
    // 공지사항 등록
    int writeNotice(Notice notice) throws SQLException;
    
    // 공지사항 수정
    int modifyNotice(Notice notice) throws SQLException;
    
    // 공지사항 삭제
    int deleteNotice(int nno) throws SQLException;
    
    // 공지사항 상세 조회
    Notice selectDetail(int nno) throws SQLException;
    
    // 공지사항 목록 조회 (페이징)
    Page<Notice> search(SearchCondition condition) throws SQLException;
    
    // 조회수 증가
    int increaseViewCount(int nno) throws SQLException;
    
    // 중요 공지사항 조회
    java.util.List<Notice> getImportantNotices() throws SQLException;
    
    // 최신 공지사항 조회 (홈페이지용)
    java.util.List<Notice> getRecentNotices(int limit) throws SQLException;
}