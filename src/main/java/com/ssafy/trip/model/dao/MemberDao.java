package com.ssafy.trip.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.SearchCondition;

@Mapper
public interface MemberDao {
    int insert(Member member);

    Member login(@Param("id") String id, @Param("password") String password);

    void modify(@Param("id") String id, @Param("name") String name, 
                @Param("password") String password, @Param("address") String address, 
                @Param("tel") String tel) ;

    int delete(@Param("id") String id, @Param("password") String password);

    String find(@Param("id") String id, @Param("tel") String tel) ;

    // 모든 맴버
    List<Member> searchAll() ;

    // 검색 맴버
    List<Member> search(SearchCondition condition) ;

    int getTotalCount(SearchCondition condition);

    Member selectDetail(@Param("id") String id) ;
}