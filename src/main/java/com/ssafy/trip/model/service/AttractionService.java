package com.ssafy.trip.model.service;

import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dto.Attraction;

public interface AttractionService {
    
    // 지역 및 콘텐츠 타입별 관광지 조회
    List<Attraction> getAttractionByAddress(String contentTypeName, String areaCode, String siGunGuCode) 
        throws Exception;
    
    // 페이징을 통한 지역 및 콘텐츠 타입별 관광지 조회
    List<Attraction> getAttractionByAddressWithPaging(String contentTypeName, String areaCode, 
        String siGunGuCode, int offset, int limit) throws Exception;
    
    // 지역 및 콘텐츠 타입별 관광지 총 개수 조회
    int getTotalAttractionCount(String contentTypeName, String areaCode, String siGunGuCode) 
        throws Exception;
    
    // 콘텐츠 타입 조회 - 타입을 Object로 변경
    List<Map<String, Object>> getContent() throws Exception;
    
    // 시도 조회 - 타입을 Object로 변경
    List<Map<String, Object>> getSido() throws Exception;
    
    // 구군 조회 - 타입을 Object로 변경
    List<Map<String, Object>> getGugun(String code) throws Exception;
    
    // 번호로 관광지 조회
    Attraction getAttractionByNo(int no) throws Exception;
    
    // 랜덤 관광지 조회
    List<Attraction> getRandomAttractions(int count) throws Exception;
    
    // 관광지 조회수 증가
    void increaseViewCount(int no) throws Exception;
    
    // 랭킹 가져오기
    List<Attraction> getRank() throws Exception;
    
    // 거리순 정렬
    Attraction[] sortAttractionListByDistance(List<Attraction> nearAttractionList, Attraction detailAttraction) 
        throws Exception;
}