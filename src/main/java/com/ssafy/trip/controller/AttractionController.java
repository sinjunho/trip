package com.ssafy.trip.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.AttractionService;
import com.ssafy.trip.model.service.BasicAttractionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attraction")
@RequiredArgsConstructor
public class AttractionController extends HttpServlet implements ControllerHelper {
    
	private static final long serialVersionUID = 1L;
    private final AttractionService aService = BasicAttractionService.getService();
    private final String keyVworld = "AFC0A287-7A73-31BB-9AA4-EB2C13CB4B1A";
    private final String keySgisServiceId = "dd717de0a4e148848192"; // 서비스 id
    private final String keySgisSecurity = "498f6a9709d2495d8512"; // 보안 key
	private final String keyData = "3O3aWqx65Brb2/663JLrcpwvOlDVASEMUmD8iFLTzypz1vNtWSuGCcGgKG7VlPZLFq8ujJR2Wkhg7a5XUpgdmg=="; // data.go.kr 인증키
//    protected void service(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String action = preProcessing(request, response);
//        switch (action) {
//        case "get-attraction-form" -> getAttraction(request, response);
//        case "getAttractionList" -> getAttractionByAddress(request, response);
//        case "getGugun" -> getGugun(request, response);  // 새 액션 추가
//        case "detailAttraction" -> getAttractionByNo(request, response);
//        case "randomDetailAttraction" -> getRandomDetailAttraction(request, response);
//        default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
//        }
//    }
    
    @GetMapping("/getGugun")
    @ResponseBody
    private ResponseEntity<List<Map<Integer, String>>> getGugun(@RequestAttribute("value") String sidoValue, Model model) {
        try {
            List<Map<Integer, String>> result = aService.getGugun(sidoValue);
            
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
            
//            Gson gson = new Gson();
//            String json = gson.toJson(result);
//            response.getWriter().write(json);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/get-attraction-form")
    private String getAttraction(Model model) {
        try {
            List<Map<Integer, String>> contentList = aService.getContent();
            List<Map<Integer, String>> sidoList = aService.getSido();
            
            // 랜덤 여행지 추천 (6개)
            List<Attraction> randomAttractions = aService.getRandomAttractions(6);
            
            model.addAttribute("contentList", contentList);
            model.addAttribute("sidoList", sidoList);
            model.addAttribute("randomAttractions", randomAttractions);
            
            return "attraction/attraction-form";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error/404";
        }
    }
    
    @GetMapping("/getAttractionList")
    private String getAttractionByAddress(@RequestParam(value = "contentTypeName", required = false) String contentTypeName,
    		@RequestParam(value = "sido", required = false) String areaCode,
            @RequestParam(value = "gugun", required = false) String siGunGuCode,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            Model model,
            HttpSession session ) {
    	
        try {
//            String contentTypeName = request.getParameter("contentTypeName");
//            String areaCode = request.getParameter("sido");
//            String siGunGuCode = request.getParameter("gugun");
        	
            
            // 페이징 파라미터 처리
//            String pageParam = request.getParameter("currentPage");
//            int currentPage = 1; // 기본값 설정
            
//            if (pageParam != null && !pageParam.isEmpty()) {
//                currentPage = Integer.parseInt(pageParam);
//            }
            
            // 검색 조건 객체 생성
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
            return "error/404";
            //request.setAttribute("error", e.getMessage());
            
        }
    }
    
    @GetMapping("/randomDetailAttraction")
    private String getRandomDetailAttraction(@RequestParam int no, Model model, HttpSession session) {
    	try {
    		System.out.println("randomDetailAttraction");
    		Attraction detailAttraction = aService.getAttractionByNo(no);
			String contentTypeName = detailAttraction.getContentTypeName();
            String areaCode = detailAttraction.getSido();
            String siGunGuCode = detailAttraction.getGugun();
            
			List<Attraction> nearAttractionList = aService.getAttractionByAddress(contentTypeName, areaCode, siGunGuCode);

//			for(int i=0;i<nearAttractionList.size();i++) {
//				System.out.println(nearAttractionList.get(i).toString());
//			}
			
			session.setAttribute("attrList", nearAttractionList);
			
			//getAttractionByNo(request, response);
			return "redirect:/attraction/detailAttraction";
			
    	} catch(Exception e) {
    		e.printStackTrace();
    		return "error/404";
    	}
    }
    
    @GetMapping("/detailAttraction")
    private String getAttractionByNo(@RequestParam int no, HttpSession session, Model model) {
		try {
			System.out.println("controller - detail : "+no);
			
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
			if(nearAttractionList != null || nearAttractionList.size()!=0) {
				Attraction[] nearAttractionArr = aService.sortAttractionListByDistance(nearAttractionList, detailAttraction);
				model.addAttribute("nearAttraction", nearAttractionArr);
			}
			//
			
			return "attraction/attraction-detail-form";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "error/404";
		}
	}
}
