package com.ssafy.trip.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.service.AttractionService;
import com.ssafy.trip.model.service.BasicAttractionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/main")
public class MainController extends HttpServlet implements ControllerHelper {
    private static final long serialVersionUID = 1L;
    private final AttractionService aService = BasicAttractionService.getService();
    
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = preProcessing(request, response);
        switch (action) {
        case "index" -> getMainPage(request, response);
        
        default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 처리
        }
    }
    
    private void getMainPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
 
            List<Attraction> topAttractions = aService.getRank();
            

            request.setAttribute("topAttractions", topAttractions);

            forward(request, response, "/index.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "인기 여행지를 불러오는 중 오류가 발생했습니다.");
            forward(request, response, "/index.jsp");
        }
    }
}