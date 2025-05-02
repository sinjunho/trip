package com.ssafy.trip.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.trip.model.dto.Board;
import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.SearchCondition;

@Mapper
public interface BoardDao {

	int insert(Board board) throws SQLException;

	List<Board> search(SearchCondition condition) throws SQLException;

	Board selectDetail(int bno) throws SQLException;

	int update(Board board) throws SQLException;

	int delete(int bno) throws SQLException;

	int getTotalCount(SearchCondition condition) throws SQLException;

	int updateViewCount(int bno) throws SQLException;
	
	List<Board> searchByTitle(SearchCondition condition) throws SQLException;
		
		
	List<Board> searchByWriter(SearchCondition condition) throws SQLException;
}
