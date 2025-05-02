package com.ssafy.trip.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController implements ControllerHelper {
    private final MemberService mService;

    @Value("${key.vworld}")
    private String keyVworld;

    @Value("${key.sgis.serviceId}")
    private String keySgisServiceId;

    @Value("${key.sgis.security}")
    private String keySgisSecurity;

    @Value("${key.data}")
    private String keyData;

    // 회원가입 폼 페이지
    @GetMapping("/regist")
    public String registForm() {
        return "member/member-regist-form";
    }

    // 로그인 폼 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "member/member-login-form";
    }

    // 마이페이지 - JWT 토큰 검증 후 접근 가능하도록 수정
    @GetMapping("/mypage")
    public String mypageForm(HttpSession session, Model model) {
        // JWT 인증 검증은 SecurityConfig와 JwtRequestFilter에서 처리됩니다.
        // 컨트롤러에서는 모델에 필요한 데이터만 전달합니다.
        return "member/member-mypage-form";
    }
    
    // 비밀번호 찾기 폼
    @GetMapping("/password")
    public String passwordForm() {
        return "member/member-password-select";
    }

    // 회원 목록 조회 - 관리자 권한 필요
    @GetMapping("/list")
    public String memberList(@RequestParam(required = false) String key, 
                             @RequestParam(required = false) String word,
                             @RequestParam(defaultValue = "1") int currentPage, 
                             Model model) {
        try {
            Map<String, String> keyMap = Map.of("1", "name", "2", "id");
            if (key != null) {
                key = keyMap.getOrDefault(key, "");
            }

            Page<Member> page = mService.search(new SearchCondition(key, word, currentPage));
            model.addAttribute("page", page);
            return "member/member-list";
        } catch (Exception e) {
            model.addAttribute("alertMsg", e.getMessage());
            return "member/member-list";
        }
    }

    // 회원 상세 정보
    @GetMapping("/detail")
    public String memberDetail(@RequestParam String id, Model model) {
        try {
            Member member = mService.selectDetail(id);
            model.addAttribute("member", member);
            model.addAttribute("key_vworld", keyVworld);
            model.addAttribute("key_sgis_service_id", keySgisServiceId);
            model.addAttribute("key_sgis_security", keySgisSecurity);
            model.addAttribute("key_data", keyData);
            return "member/member-detail";
        } catch (Exception e) {
            model.addAttribute("alertMsg", e.getMessage());
            return "member/member-detail";
        }
    }
}