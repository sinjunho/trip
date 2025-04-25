package com.ssafy.trip.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.trip.model.dto.Attraction;

@Mapper
public interface AttractionDao {
    
    // 지역 및 콘텐츠 타입별 관광지 조회
    List<Attraction> getAttractionByAddress(@Param("contentTypeName") String contentTypeName, 
                                           @Param("areaCode") String areaCode, 
                                           @Param("siGunGuCode") String siGunGuCode);
    
    // 페이징을 통한 지역 및 콘텐츠 타입별 관광지 조회
    List<Attraction> getAttractionByAddressWithPaging(@Param("contentTypeName") String contentTypeName, 
                                                     @Param("areaCode") String areaCode, 
                                                     @Param("siGunGuCode") String siGunGuCode, 
                                                     @Param("offset") int offset, 
                                                     @Param("limit") int limit);
    
    // 지역 및 콘텐츠 타입별 관광지 총 개수 조회
    int getTotalAttractionCount(@Param("contentTypeName") String contentTypeName, 
                              @Param("areaCode") String areaCode, 
                              @Param("siGunGuCode") String siGunGuCode);
    
    // 콘텐츠 타입 조회 - 반환 타입을 Object로 변경
    List<Map<String, Object>> getContent();
    
    // 시도 조회 - 반환 타입을 Object로 변경
    List<Map<String, Object>> getSido();
    
    // 구군 조회 - 반환 타입을 Object로 변경
    List<Map<String, Object>> getGugun(@Param("code") String code);
    
    // 번호로 관광지 조회
    Attraction getAttractionByNo(@Param("no") int no);
    
    // 랜덤 관광지 조회
    List<Attraction> getRandomAttractions(@Param("count") int count);
    
    // 관광지 조회수 증가
    int updateViewCount(@Param("no") int no);
    
    // 조회수 있는 모든 관광지 조회
    List<Attraction> allCountView();
}