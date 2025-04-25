package com.ssafy.trip.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

public interface MemberService {

    int registMember(Member member) throws SQLException;

    Member login(String id, String password) throws SQLException;

	void modifyMember(String id, String name, String password, String address, String tel) throws SQLException;

	int deleteMember(String id, String password)throws SQLException;

	String find(String id, String tel)throws SQLException;

	List<Member> search() throws SQLException;
	
	Page<Member> search(SearchCondition searchCondition) throws SQLException;

	Member selectDetail(String id)throws SQLException;
	
//	String getSalt();
}
