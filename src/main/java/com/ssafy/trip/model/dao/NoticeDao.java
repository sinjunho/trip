package com.ssafy.trip.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.trip.model.dto.Notice;
import com.ssafy.trip.model.dto.SearchCondition;

@Mapper
public interface NoticeDao {
    
    // 공지사항 등록
    int insert(Notice notice) throws SQLException;
    
    // 공지사항 수정
    int update(Notice notice) throws SQLException;
    
    // 공지사항 삭제
    int delete(int nno) throws SQLException;
    
    // 공지사항 상세 조회
    Notice selectDetail(int nno) throws SQLException;
    
    // 공지사항 목록 조회 (페이징)
    List<Notice> search(SearchCondition condition) throws SQLException;
    
    // 전체 공지사항 수 조회
    int getTotalCount(SearchCondition condition) throws SQLException;
    
    // 조회수 증가
    int updateViewCount(int nno) throws SQLException;
    
    // 중요 공지사항만 조회
    List<Notice> getImportantNotices() throws SQLException;
    
    // 활성화된 공지사항만 조회
    List<Notice> getActiveNotices() throws SQLException;
    
    // 최신 공지사항 조회 (홈페이지용)
    List<Notice> getRecentNotices(int limit) throws SQLException;
}