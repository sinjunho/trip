package com.ssafy.trip.model.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.trip.model.dao.BoardDao;
import com.ssafy.trip.model.dto.Board;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicBoardService implements BoardService {
    private final BoardDao dao;

    @Override
    public int writeBoard(Board board) throws SQLException {
    	return dao.insert(board);
    }

    @Override
    public Page<Board> search(SearchCondition condition) throws SQLException {
    	int totalItems = dao.getTotalCount(condition);
    	List<Board> boards = dao.search(condition);
    	return new Page<>(condition, totalItems, boards);
    }

    @Override
    public Board selectDetail(int bno) throws SQLException {
    	return dao.selectDetail(bno);
    }

    @Override
    public int modifyBoard(Board board) throws SQLException {
    	return dao.update(board);
    }

    @Override
    public int deleteBoard(int bno) throws SQLException {
    	return dao.delete(bno);
    }
    
    @Override
    public int increaseViewCount(int bno) throws SQLException {
    	return dao.updateViewCount(bno);
    }
    
    @Override
    public Page<Board> searchByTitle(SearchCondition condition) throws SQLException {
    	int totalItems = dao.getTotalCount(condition);
    	List<Board> boards = dao.searchByTitle(condition);
    	return new Page<>(condition, totalItems, boards);
    }
    
    @Override
    public Page<Board> searchByWriter(SearchCondition condition) throws SQLException {
    	int totalItems = dao.getTotalCount(condition);
    	List<Board> boards = dao.searchByWriter(condition);
    	return new Page<>(condition, totalItems, boards);
    }
}
