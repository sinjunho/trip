package com.ssafy.trip.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.SearchCondition;

public interface MemberDao {
	int insert(Connection con, Member member) throws SQLException;

	Member login(Connection con, String id, String password) throws SQLException;

	void modify(Connection con, String id, String name, String password, String address, String tel)
			throws SQLException;

	int delete(Connection con, String id, String password) throws SQLException;

	String find(Connection con, String id, String tel) throws SQLException;

	List<Member> search(Connection con) throws SQLException;

	public List<Member> search(Connection con, SearchCondition condition) throws SQLException;

	public int getTotalCount(Connection con, SearchCondition condition) throws SQLException;

	Member selectDetail(Connection con, String id) throws SQLException;

}
