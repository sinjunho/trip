package com.ssafy.trip.controller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
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

	// 회원가입 처리
	@PostMapping("/regist")
	public String registMember(Member member, RedirectAttributes redirectAttributes) {
		try {
			mService.registMember(member);
			redirectAttributes.addFlashAttribute("alertMsg", "등록되었습니다. 로그인 후 사용해주세요.");
			return "redirect:/member/login";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "아마도 아이디 겹칠걸?");
			return "redirect:/member/regist";
		}
	}

	// 로그인 폼 페이지
	@GetMapping("/login")
	public String loginForm() {
		return "member/member-login-form";
	}

	// 로그인 처리
	@PostMapping("/login")
	public String loginMember(@RequestParam String id, @RequestParam String password,
			@RequestParam(required = false) String rememberMe, HttpSession session, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			Member member = mService.login(id, password);

			// 아이디 기억하기 처리
			if (rememberMe == null) {
				Cookie cookie = new Cookie("rememberMe", id);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			} else {
				Cookie cookie = new Cookie("rememberMe", id);
				cookie.setMaxAge(60 * 60 * 10); // 10시간
				response.addCookie(cookie);
			}

			if (member == null) {
				redirectAttributes.addFlashAttribute("alertMsg", "로그인 실패 되었습니다.");
				return "redirect:/member/login";
			} else {
				session.setAttribute("member", member);
				redirectAttributes.addFlashAttribute("alertMsg", "로그인이 완료 되었습니다.");
				return "redirect:/";
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/member/login";
		}
	}

	// 마이페이지
	@GetMapping("/mypage")
	public String mypageForm() {
		return "member/member-mypage-form";
	}

	// 회원 정보 수정
	@PostMapping("/modify")
	public String modifyMember(@RequestParam String name, @RequestParam String password, @RequestParam String address,
			@RequestParam String tel, HttpSession session, RedirectAttributes redirectAttributes) {
		try {
			Member member = (Member) session.getAttribute("member");
			String id = member.getId();

			mService.modifyMember(id, name, password, address, tel);

			// 세션 업데이트
			member.setName(name);
			member.setPassword(password);
			member.setAddress(address);
			member.setTel(tel);
			session.setAttribute("member", member);

			redirectAttributes.addFlashAttribute("alertMsg", "수정이 완료 되었습니다.");
			return "redirect:/member/mypage";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/member/mypage";
		}
	}

	// 회원 탈퇴
	@PostMapping("/delete")
	public String deleteMember(@RequestParam String password, @RequestParam String password2, HttpSession session,
			RedirectAttributes redirectAttributes) {
		try {
			Member member = (Member) session.getAttribute("member");
			String id = member.getId();

			int result = -1;
			if (password.equals(password2)) {
				result = mService.deleteMember(id, password);
			}

			if (result == -1) {
				redirectAttributes.addFlashAttribute("alertMsg", "삭제 실패 비밀번호를 확인 해주십쇼.");
				return "redirect:/member/mypage";
			} else {
				session.removeAttribute("member");
				session.invalidate();
				redirectAttributes.addFlashAttribute("alertMsg", "삭제가 완료 되었습니다.");
				return "redirect:/";
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/member/mypage";
		}
	}

	// 관리자가 회원 삭제
	@PostMapping("/admin/delete")
	public String deleteMemberAdmin(@RequestParam String id, @RequestParam String password,
			RedirectAttributes redirectAttributes) {
		try {
			int result = mService.deleteMember(id, password);

			if (result == 1) {
				redirectAttributes.addFlashAttribute("alertMsg", "삭제가 완료 되었습니다.");
			} else {
				redirectAttributes.addFlashAttribute("alertMsg", "삭제 실패.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/member/list";
	}

	// 로그아웃
	@GetMapping("/logout")
	public String logoutMember(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	// 비밀번호 찾기 폼
	@GetMapping("/password")
	public String passwordForm() {
		return "member/member-password-select";
	}

	// 비밀번호 찾기 처리
	@PostMapping("/find-password")
	public String findPassword(@RequestParam String id, @RequestParam String tel, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			String password = mService.find(id, tel);

			if (password == null) {
				redirectAttributes.addFlashAttribute("alertMsg", "비밀번호 못찾았습니다 실패 되었습니다.");
				return "redirect:/member/password";
			} else {
				redirectAttributes.addFlashAttribute("alertMsg", "당신의 비밀번호는? : " + password);
				return "redirect:/member/login";
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/member/login";
		}
	}

	// 회원 목록 조회
	@GetMapping("/list")
	public String memberList(@RequestParam(required = false) String key, @RequestParam(required = false) String word,
			@RequestParam(defaultValue = "1") int currentPage, Model model) {
		try {
			Map<String, String> keyMap = Map.of("1", "name", "2", "id");
			if (key != null) {
				key = keyMap.getOrDefault(key, "");
			}

			Page<Member> page = mService.search(new SearchCondition(key, word, currentPage));
			model.addAttribute("page", page);
			return "member/member-list";
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
			model.addAttribute("alertMsg", e.getMessage());
			return "member/member-detail";
		}
	}

}
