package com.ssafy.trip.model.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dto.Attraction;

public interface AttractionService {

    List<Attraction> getAttractionByAddress(String contentTypeName, String area_code, String si_gun_gu_code) throws SQLException;
    
    List<Map<Integer, String>> getContent() throws SQLException;
    
    List<Map<Integer, String>> getSido() throws SQLException;
    
    List<Map<Integer, String>> getGugun(String code) throws SQLException;
    
    Attraction getAttractionByNo(int no) throws SQLException;
    
    List<Attraction> getAttractionByAddressWithPaging(String contentTypeName, String areaCode, String siGunGuCode, int offset, int limit) throws SQLException;
    
    int getTotalAttractionCount(String contentTypeName, String areaCode, String siGunGuCode) throws SQLException;
    
    List<Attraction> getRandomAttractions(int count) throws SQLException;

	void increaseViewCount(int no) throws SQLException;

	List<Attraction> getRank() throws SQLException;

	Attraction[] sortAttractionListByDistance(List<Attraction> inNearAttractionList, Attraction detailAttraction) throws SQLException;

}
