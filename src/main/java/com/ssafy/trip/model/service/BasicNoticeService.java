package com.ssafy.trip.model.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.trip.model.dao.NoticeDao;
import com.ssafy.trip.model.dto.Notice;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicNoticeService implements NoticeService {
    
    private final NoticeDao dao;
    
    @Override
    public int writeNotice(Notice notice) throws SQLException {
        return dao.insert(notice);
    }
    
    @Override
    public int modifyNotice(Notice notice) throws SQLException {
        return dao.update(notice);
    }
    
    @Override
    public int deleteNotice(int nno) throws SQLException {
        return dao.delete(nno);
    }
    
    @Override
    public Notice selectDetail(int nno) throws SQLException {
        return dao.selectDetail(nno);
    }
    
    @Override
    public Page<Notice> search(SearchCondition condition) throws SQLException {
        int totalItems = dao.getTotalCount(condition);
        List<Notice> notices = dao.search(condition);
        return new Page<>(condition, totalItems, notices);
    }
    
    @Override
    public int increaseViewCount(int nno) throws SQLException {
        return dao.updateViewCount(nno);
    }
    
    @Override
    public List<Notice> getImportantNotices() throws SQLException {
        return dao.getImportantNotices();
    }
    
    @Override
    public List<Notice> getRecentNotices(int limit) throws SQLException {
        return dao.getRecentNotices(limit);
    }
}