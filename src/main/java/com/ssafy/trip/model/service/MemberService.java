package com.ssafy.trip.model.service;

import java.util.List;
import java.util.Map;

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
	
	
	  // 전체 회원 수 조회
    int getTotalMemberCount() throws Exception;
//	String getSalt();
    
    // 관리자 전용 기능
    int updateMemberRole(String id, String role) throws Exception;
    int forceDeleteMember(String id) throws Exception;
    List<Member> getRecentMembers(int limit) throws Exception;
    Map<String, Object> getMemberStatistics() throws Exception;
}
