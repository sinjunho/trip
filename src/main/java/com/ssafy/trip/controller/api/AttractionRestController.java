package com.ssafy.trip.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.service.AttractionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
@Tag(name = "관광지 관리", description = "관광지 정보 관련 API")
public class AttractionRestController {
    
    private final AttractionService attractionService;
    
    // 키워드 검색 메서드 추가
    @GetMapping("/search")
    public ResponseEntity<?> searchAttractionsByKeyword(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String contentTypeName,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String siGunGuCode,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Attraction> attractions;
            int totalCount = 0;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 키워드로 검색
                attractions = attractionService.searchAttractionsByTitle(keyword, contentTypeName, areaCode, siGunGuCode, offset, limit);
                totalCount = attractionService.getSearchCount(keyword, contentTypeName, areaCode, siGunGuCode);
            } else {
                // 기본 필터 검색
                attractions = attractionService.getAttractionByAddressWithPaging(contentTypeName, areaCode, siGunGuCode, offset, limit);
                totalCount = attractionService.getTotalAttractionCount(contentTypeName, areaCode, siGunGuCode);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("attractions", attractions);
            result.put("totalCount", totalCount);
            result.put("currentPage", (offset / limit) + 1);
            result.put("totalPages", (int) Math.ceil((double) totalCount / limit));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "검색 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "관광지 목록 조회", description = "검색 조건에 맞는 관광지 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "관광지 목록 조회 성공")
    public ResponseEntity<?> getAttractions(
            @Parameter(description = "컨텐츠 타입") 
            @RequestParam(required = false) String contentTypeName,
            @Parameter(description = "지역 코드") 
            @RequestParam(required = false) String areaCode,
            @Parameter(description = "시군구 코드") 
            @RequestParam(required = false) String siGunGuCode,
            @Parameter(description = "시작 위치") 
            @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "가져올 개수") 
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // 로그 추가
            System.out.println("요청 파라미터 - contentTypeName: " + contentTypeName + 
                              ", areaCode: " + areaCode + 
                              ", siGunGuCode: " + siGunGuCode + 
                              ", offset: " + offset + 
                              ", limit: " + limit);
            
            List<Attraction> attractions = attractionService.getAttractionByAddressWithPaging(
                contentTypeName, areaCode, siGunGuCode, offset, limit);
            
            // 결과 로그 추가
            System.out.println("검색 결과 개수: " + (attractions != null ? attractions.size() : 0));
            
            return ResponseEntity.ok(attractions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "관광지 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "관광지 상세 조회", description = "특정 관광지의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "관광지 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "관광지를 찾을 수 없음")
    public ResponseEntity<?> getAttraction(
            @Parameter(description = "조회할 관광지 ID", required = true) 
            @PathVariable int id) {
        try {
            Attraction attraction = attractionService.getAttractionByNo(id);
            if (attraction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "관광지를 찾을 수 없습니다."));
            }
            
            // 조회수 증가
            attractionService.increaseViewCount(id);
            
            return ResponseEntity.ok(attraction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "관광지 상세 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/random")
    @Operation(summary = "랜덤 관광지 조회", description = "지정된 개수만큼 랜덤 관광지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "랜덤 관광지 조회 성공")
    public ResponseEntity<?> getRandomAttractions(
            @Parameter(description = "가져올 관광지 개수") 
            @RequestParam(defaultValue = "8") int count) {
        try {
            List<Attraction> attractions = attractionService.getRandomAttractions(count);
            return ResponseEntity.ok(attractions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "랜덤 관광지 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/content-types")
    @Operation(summary = "콘텐츠 타입 조회", description = "모든 콘텐츠 타입 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "콘텐츠 타입 조회 성공")
    public ResponseEntity<?> getContentTypes() {
        try {
            List<Map<String, Object>> contentTypes = attractionService.getContent();
            return ResponseEntity.ok(contentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "콘텐츠 타입 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/sido")
    @Operation(summary = "시도 목록 조회", description = "모든 시도 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "시도 목록 조회 성공")
    public ResponseEntity<?> getSido() {
        try {
            List<Map<String, Object>> sidoList = attractionService.getSido();
            return ResponseEntity.ok(sidoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "시도 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/gugun/{code}")
    @Operation(summary = "구군 목록 조회", description = "특정 시도에 속한 구군 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "구군 목록 조회 성공")
    public ResponseEntity<?> getGugun(
            @Parameter(description = "시도 코드", required = true) 
            @PathVariable String code) {
        try {
            List<Map<String, Object>> gugunList = attractionService.getGugun(code);
            return ResponseEntity.ok(gugunList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "구군 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/rank")
    @Operation(summary = "인기 관광지 조회", description = "조회수 기준 인기 관광지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 관광지 조회 성공")
    public ResponseEntity<?> getRankAttractions() {
        try {
            List<Attraction> attractions = attractionService.getRank();
            return ResponseEntity.ok(attractions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "인기 관광지 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    
    @GetMapping("/near/{id}")
    @Operation(summary = "주변 관광지 조회", description = "특정 관광지 주변의 관광지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주변 관광지 조회 성공")
    @ApiResponse(responseCode = "404", description = "관광지를 찾을 수 없음")
    public ResponseEntity<?> getNearbyAttractions(
            @Parameter(description = "기준 관광지 ID", required = true) 
            @PathVariable int id) {
        try {
            Attraction attraction = attractionService.getAttractionByNo(id);
            if (attraction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "기준 관광지를 찾을 수 없습니다."));
            }
            
            // 같은 지역 관광지 목록 조회
            List<Attraction> nearAttractions = attractionService.getAttractionByAddress(
                attraction.getContentTypeName(), attraction.getSido(), attraction.getGugun());
            
            // 거리순 정렬
            Attraction[] sortedAttractions = attractionService.sortAttractionListByDistance(
                nearAttractions, attraction);
            
            return ResponseEntity.ok(sortedAttractions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "주변 관광지 조회 중 오류 발생: " + e.getMessage()));
        }
    }
    


    @GetMapping("/popular-cities")
    @Operation(summary = "인기 도시 조회", description = "관광지 수가 많은 인기 도시 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 도시 조회 성공")
    public ResponseEntity<?> getPopularCities() {
        try {
            List<Map<String, Object>> popularCities = attractionService.getPopularCities();
            return ResponseEntity.ok(popularCities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "인기 도시 조회 중 오류 발생: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "관광지 통계 조회", description = "전체 관광지 수 등 통계 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "통계 조회 성공")
    public ResponseEntity<?> getAttractionStatistics() {
        try {
            Map<String, Object> statistics = attractionService.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "통계 조회 중 오류 발생: " + e.getMessage()));
        }
    }
}