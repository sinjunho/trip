package com.ssafy.trip.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

@WebServlet("/attraction")
public class AttractionController extends HttpServlet implements ControllerHelper {
    private static final long serialVersionUID = 1L;
    private final AttractionService aService = BasicAttractionService.getService();
    private final String keyVworld = "AFC0A287-7A73-31BB-9AA4-EB2C13CB4B1A";
    private final String keySgisServiceId = "dd717de0a4e148848192"; // 서비스 id
    private final String keySgisSecurity = "498f6a9709d2495d8512"; // 보안 key
	private final String keyData = "3O3aWqx65Brb2/663JLrcpwvOlDVASEMUmD8iFLTzypz1vNtWSuGCcGgKG7VlPZLFq8ujJR2Wkhg7a5XUpgdmg=="; // data.go.kr 인증키
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = preProcessing(request, response);
        switch (action) {
        case "get-attraction-form" -> getAttraction(request, response);
        case "getAttractionList" -> getAttractionByAddress(request, response);
        case "getGugun" -> getGugun(request, response);  // 새 액션 추가
        case "detailAttraction" -> getAttractionByNo(request, response);
        case "randomDetailAttraction" -> getRandomDetailAttraction(request, response);
        default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void getGugun(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String sidoValue = request.getParameter("value");
            List<Map<Integer, String>> result = aService.getGugun(sidoValue);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            Gson gson = new Gson();
            String json = gson.toJson(result);
            response.getWriter().write(json);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    private void getAttraction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Map<Integer, String>> contentList = aService.getContent();
            List<Map<Integer, String>> sidoList = aService.getSido();
            
            // 랜덤 여행지 추천 (6개)
            List<Attraction> randomAttractions = aService.getRandomAttractions(6);
            
            request.getSession().setAttribute("contentList", contentList);
            request.getSession().setAttribute("sidoList", sidoList);
            request.setAttribute("randomAttractions", randomAttractions);
            
            forward(request, response, "/attraction/attraction-form.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
        }
    }
    
    private void getAttractionByAddress(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String contentTypeName = request.getParameter("contentTypeName");
            String areaCode = request.getParameter("sido");
            String siGunGuCode = request.getParameter("gugun");
            
            // 페이징 파라미터 처리
            String pageParam = request.getParameter("currentPage");
            int currentPage = 1; // 기본값 설정
            
            if (pageParam != null && !pageParam.isEmpty()) {
                currentPage = Integer.parseInt(pageParam);
            }
            
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
            
            request.getSession().setAttribute("attrList", pagedList);  // 페이징된 리스트
            request.setAttribute("page", page);  // 페이지 객체 추가
            request.setAttribute("key_vworld", keyVworld);
            request.setAttribute("key_sgis_service_id", keySgisServiceId);
            request.setAttribute("key_sgis_security", keySgisSecurity);
            request.setAttribute("key_data", keyData);
            
            forward(request, response, "/attraction/map-list.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
        }
    }
    
    private void getRandomDetailAttraction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		System.out.println("randomDetailAttraction");
    		int no = Integer.parseInt(request.getParameter("no"));
    		Attraction detailAttraction = aService.getAttractionByNo(no);
			String contentTypeName = detailAttraction.getContentTypeName();
            String areaCode = detailAttraction.getSido();
            String siGunGuCode = detailAttraction.getGugun();
            
			List<Attraction> nearAttractionList = aService.getAttractionByAddress(contentTypeName, areaCode, siGunGuCode);

//			for(int i=0;i<nearAttractionList.size();i++) {
//				System.out.println(nearAttractionList.get(i).toString());
//			}
			
			request.getSession().setAttribute("attrList", nearAttractionList);
			
			getAttractionByNo(request, response);
			
    	} catch(Exception e) {
    		e.printStackTrace();
    		request.setAttribute("error", e.getMessage());
    	}
    }
    
   private void getAttractionByNo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int no = Integer.parseInt(request.getParameter("no"));
			System.out.println("controller - detail : "+no);
			
			HttpSession session = request.getSession();
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
			request.getSession().setAttribute("detailAttraction", detailAttraction); 
			
			// 주변 관광지 거리순으로 보여주기
			List<Attraction> nearAttractionList = (List<Attraction>) request.getSession().getAttribute("attrList");
			if(nearAttractionList != null || nearAttractionList.size()!=0) {
				Attraction[] nearAttractionArr = aService.sortAttractionListByDistance(nearAttractionList, detailAttraction);
				request.setAttribute("nearAttraction", nearAttractionArr);
			}
			//
			
			forward(request, response, "/attraction/attraction-detail-form.jsp");
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
		}
	}
}
