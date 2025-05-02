package com.ssafy.trip.model.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.ssafy.trip.model.dto.Board;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

public interface BoardService {

	int writeBoard(Board board) throws SQLException;

	Page<Board> search(SearchCondition searchCondition) throws SQLException;

	Board selectDetail(int bno) throws SQLException;

	int modifyBoard(Board board) throws SQLException;

	int deleteBoard(int bno) throws SQLException;

	int increaseViewCount(int bno) throws SQLException;
	
	Page<Board> searchByTitle(SearchCondition condition) throws SQLException;
	
	Page<Board> searchByWriter(SearchCondition condition) throws SQLException;
}
