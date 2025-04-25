package com.ssafy.trip.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.dto.Member;

public interface AttractionDao {
	List<Attraction> getAttractionByAddress(Connection con, String contentTypeName, String areaCode, String siGunGuCode, int offset, int limit) throws SQLException;
	
	List<Map<Integer, String>> getContent(Connection con) throws SQLException;
	
	List<Map<Integer, String>> getSido(Connection con) throws SQLException;
	
	List<Map<Integer, String>> getGugun(Connection con, String code) throws SQLException;
	
	Attraction getAttractionByNo(Connection con, int no) throws SQLException;
	
	List<Attraction> getAttractionByAddressWithPaging(Connection con, String contentTypeName, String areaCode, String siGunGuCode, int offset, int limit) throws SQLException;
	
	int getTotalAttractionCount(Connection con, String contentTypeName, String areaCode, String siGunGuCode) throws SQLException;

	List<Attraction> getRandomAttractions(Connection con, int count) throws SQLException;

	int updateViewCount(Connection con, int no) throws SQLException;

	List<Attraction> allCountView(Connection con) throws SQLException;
}
