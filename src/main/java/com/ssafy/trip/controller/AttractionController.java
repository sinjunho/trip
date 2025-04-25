package com.ssafy.trip.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.AttractionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attraction")
@RequiredArgsConstructor
public class AttractionController implements ControllerHelper {
    private final AttractionService aService;
    
    @Value("${key.vworld}")
    private String keyVworld;

    @Value("${key.sgis.serviceId}")
    private String keySgisServiceId;

    @Value("${key.sgis.security}")
    private String keySgisSecurity;

    @Value("${key.data}")
    private String keyData;

    @GetMapping("/getGugun")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getGugun(@RequestParam("sido") String sidoValue, Model model) {
        try {
            System.out.println("시도값으로 구군 조회: " + sidoValue);
            
            // 시도값이 숫자(코드)인지 문자(이름)인지 확인
            boolean isNumeric = sidoValue.matches("\\d+");
            System.out.println("시도값 형식: " + (isNumeric ? "코드(숫자)" : "이름(문자)"));
            
            // 원래 서비스 메소드 호출
            List<Map<String, Object>> result = aService.getGugun(sidoValue);
            
            System.out.println("구군 조회 결과 크기: " + (result != null ? result.size() : "null"));
            if (result != null && result.size() > 0) {
                System.out.println("첫 번째 구군 항목: " + result.get(0));
            }
            
            // 응답 형식 변환 (구군 이름만 반환)
            List<Map<String, String>> simplifiedResult = new ArrayList<>();
            if (result != null) {
                for (Map<String, Object> item : result) {
                    Map<String, String> simpleMap = new HashMap<>();
                    // gugun_name 키가 있는지 확인
                    Object gugunName = item.get("gugun_name");
                    if (gugunName != null) {
                        simpleMap.put("name", String.valueOf(gugunName));
                        simplifiedResult.add(simpleMap);
                    }
                }
            }
            
            System.out.println("변환된 구군 데이터: " + simplifiedResult);
            return ResponseEntity.ok(simplifiedResult);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/get-attraction-form")
    private String getAttraction(HttpSession session, Model model) {
        try {
            List<Map<String, Object>> contentListTemp = aService.getContent();
            List<Map<Integer, String>> contentList = new ArrayList<>();
            
            for (Map<String, Object> row : contentListTemp) {
                Map<Integer, String> convertedMap = new HashMap<>();
                Integer key = (Integer) row.get("content_type_id");
                String value = (String) row.get("content_type_name");
                convertedMap.put(key, value);
                contentList.add(convertedMap);
            }
            System.out.println("contentList : "+contentList);
            
            List<Map<String, Object>> sidoListTemp = aService.getSido();

            List<Map<Integer, String>> sidoList = new ArrayList<>();
            for (Map<String, Object> row : sidoListTemp) {
                Map<Integer, String> convertedMap = new HashMap<>();
                Integer key = (Integer) row.get("sido_code");
                String value = (String) row.get("sido_name");
                convertedMap.put(key, value);
                sidoList.add(convertedMap);
            }
            System.out.println("sidoList : "+sidoList);
            
            // 랜덤 여행지 추천 (6개)
            List<Attraction> randomAttractions = aService.getRandomAttractions(6);
            System.out.println("randomAttractions : "+randomAttractions.size());
            for(Attraction a : randomAttractions) {
                System.out.println(a.toString());
            }
            
            session.setAttribute("contentList", contentList);
            session.setAttribute("sidoList", sidoList);
            model.addAttribute("randomAttractions", randomAttractions);
            
            return "attraction/attraction-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
//            return "index";
            return "attraction/attraction-form";
        }
    }
    
    @GetMapping("/getAttractionList")
    public String getAttractionByAddress(
            @RequestParam(value = "contentTypeName", required = false) String contentTypeName,
            @RequestParam(value = "sido", required = false) String areaCode,
            @RequestParam(value = "gugun", required = false) String siGunGuCode,
            @RequestParam(defaultValue = "1") int currentPage,
            Model model,
            HttpSession session) {
        
        try {
            System.out.println("조회 요청 - 컨텐츠: " + contentTypeName + ", 시도: " + areaCode + ", 구군: " + siGunGuCode);

            SearchCondition condition = new SearchCondition();
            condition.setCurrentPage(currentPage);
            
            // 전체 데이터 수 조회
            int totalItems = aService.getTotalAttractionCount(contentTypeName, areaCode, siGunGuCode);
            
            // 페이징 처리된 데이터 조회
            List<Attraction> pagedList = aService.getAttractionByAddressWithPaging(
                contentTypeName, areaCode, siGunGuCode, 
                condition.getOffset(), condition.getItemsPerPage());
            
            // Page 객체 생성
            Page<Attraction> page = new Page<>(condition, totalItems, pagedList);
            
            session.setAttribute("attrList", pagedList);  // 페이징된 리스트
            model.addAttribute("page", page);  // 페이지 객체 추가
            model.addAttribute("key_vworld", keyVworld);
            model.addAttribute("key_sgis_service_id", keySgisServiceId);
            model.addAttribute("key_sgis_security", keySgisSecurity);
            model.addAttribute("key_data", keyData);
            
            return "attraction/map-list";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/404";  
        }
    }
    
    @GetMapping("/randomDetailAttraction")
    public String getRandomDetailAttraction(@RequestParam int no, Model model, HttpSession session) {
        try {
            System.out.println("randomDetailAttraction - no: " + no);
            Attraction detailAttraction = aService.getAttractionByNo(no);
            String contentTypeName = detailAttraction.getContentTypeName();
            String areaCode = detailAttraction.getSido();
            String siGunGuCode = detailAttraction.getGugun();
            
            List<Attraction> nearAttractionList = aService.getAttractionByAddress(contentTypeName, areaCode, siGunGuCode);
            
            session.setAttribute("attrList", nearAttractionList);
            session.setAttribute("no", no);  // no 파라미터 세션에 저장
            
            return "redirect:/attraction/detailAttraction?no=" + no;
            
        } catch(Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/404";
        }
    }
    
    @GetMapping("/detailAttraction")
    public String getAttractionByNo(@RequestParam int no, HttpSession session, Model model) {
        try {
            System.out.println("controller - detail : " + no);
            
            Set<Integer> readArticles = (Set<Integer>) session.getAttribute("readArticles");
            
            if (readArticles == null) {
                readArticles = new HashSet<>();
                session.setAttribute("readArticles", readArticles);
            }
            
            if (!readArticles.contains(no)) {
                aService.increaseViewCount(no);
                readArticles.add(no);
            }
            
            Attraction detailAttraction = aService.getAttractionByNo(no);
            System.out.println(detailAttraction.toString());
            session.setAttribute("detailAttraction", detailAttraction); 
            
            // 주변 관광지 거리순으로 보여주기
            List<Attraction> nearAttractionList = (List<Attraction>) session.getAttribute("attrList");
            if (nearAttractionList != null && !nearAttractionList.isEmpty()) {
                Attraction[] nearAttractionArr = aService.sortAttractionListByDistance(nearAttractionList, detailAttraction);
                model.addAttribute("nearAttraction", nearAttractionArr);
            }
            
            return "attraction/attraction-detail-form";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/404";
        }
    }
}