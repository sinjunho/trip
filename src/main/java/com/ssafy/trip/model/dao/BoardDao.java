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

	int insert(Connection con, Board board) throws SQLException;

	List<Board> search(Connection con, SearchCondition condition) throws SQLException;

	Board selectDetail(Connection con, int bno) throws SQLException;

	int update(Connection con, Board board) throws SQLException;

	int delete(Connection con, int bno) throws SQLException;

	int getTotalCount(Connection con, SearchCondition condition) throws SQLException;

	int updateViewCount(Connection con, int bno) throws SQLException;
	
	List<Board> searchByTitle(Connection con, SearchCondition condition) throws SQLException;
		
		
	List<Board> searchByWriter(Connection con, SearchCondition condition) throws SQLException;
}
