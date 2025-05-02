package com.ssafy.trip.controller.api;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
public class AttractionRestController {
    
    private final AttractionService attractionService;
    
    @GetMapping
    public ResponseEntity<?> getAttractions(
            @RequestParam(required = false) String contentTypeName,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String siGunGuCode,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Attraction> attractions = attractionService.getAttractionByAddressWithPaging(
                contentTypeName, areaCode, siGunGuCode, offset, limit);
            
            return ResponseEntity.ok(attractions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행지를 가져오는 데 실패 했습니다. : " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAttraction(@PathVariable int id) {
        try {
            Attraction attraction = attractionService.getAttractionByNo(id);
            if (attraction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "ID로 어트랙션을 찾을 수 없습니다 : " + id));
            }
            
            // Update view count
            attractionService.increaseViewCount(id);
            
            return ResponseEntity.ok(attraction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "여행지를 가져오는 데 실패 했습니다. :  " + e.getMessage()));
        }
    }
    
    @GetMapping("/random")
    public ResponseEntity<?> getRandomAttractions(@RequestParam(defaultValue = "6") int count) {
        try {
            List<Attraction> attractions = attractionService.getRandomAttractions(count);
            return ResponseEntity.ok(attractions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "랜덤 여행지를 가져오는 데 실패 했습니다. :  " + e.getMessage()));
        }
    }
    
    @GetMapping("/content-types")
    public ResponseEntity<?> getContentTypes() {
        try {
            List<Map<String, Object>> contentTypes = attractionService.getContent();
            return ResponseEntity.ok(contentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "콘텐츠 유형을 가져오지 못했습니다 : " + e.getMessage()));
        }
    }
    
    @GetMapping("/sido")
    public ResponseEntity<?> getSido() {
        try {
            List<Map<String, Object>> sidoList = attractionService.getSido();
            return ResponseEntity.ok(sidoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "시도 목록을 가져오지 못했습니다 : " + e.getMessage()));
        }
    }
    
    @GetMapping("/gugun/{code}")
    public ResponseEntity<?> getGugun(@PathVariable String code) {
        try {
            List<Map<String, Object>> gugunList = attractionService.getGugun(code);
            return ResponseEntity.ok(gugunList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "구군 목록을 가져오지 못했습니다 : " + e.getMessage()));
        }
    }
}