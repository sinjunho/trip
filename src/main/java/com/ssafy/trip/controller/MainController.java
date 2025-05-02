package com.ssafy.trip.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.service.AttractionService;

import jakarta.servlet.http.HttpServlet;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController implements ControllerHelper {
   private final AttractionService aService;
    
    
    @GetMapping({"/","/index"})
    private String getMainPage(Model model)  {
        try {
            List<Attraction> topAttractions = aService.getRank();
            
            model.addAttribute("topAttractions", topAttractions);

            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "인기 여행지를 불러오는 중 오류가 발생했습니다.");
            return "index";
        }
    }
}