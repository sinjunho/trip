package com.ssafy.trip.model.service;

import java.util.List;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

public interface MemberService {

    int registMember(Member member);

    Member login(String id, String password);

	void modifyMember(String id, String name, String password, String address, String tel) ;

	int deleteMember(String id, String password);

	String find(String id, String tel);

	List<Member> searchAll() ;
	
	Page<Member> search(SearchCondition searchCondition);

	Member selectDetail(String id);
	
//	String getSalt();
}
