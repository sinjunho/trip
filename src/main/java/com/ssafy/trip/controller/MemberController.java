package com.ssafy.trip.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.BasicMemberService;
import com.ssafy.trip.model.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/member")
public class MemberController extends HttpServlet implements ControllerHelper {
	private static final long serialVersionUID = 1L;
	private final MemberService mService = BasicMemberService.getService();

	private final String keyVworld = " C734D4B2-B837-390D-A66B-1771B77AE796";
	 private final String keySgisServiceId = "f59d48bc2fe64501be79"; // 서비스 id
	 private final String keySgisSecurity = "3318faf72e43446abda0"; // 보안 key
	 private final String keyData = "q5KMtOGhU1LB4Ohv1eStvWgru+8V+as9yT22qLzKtpZVJGpDb4JNtFNDZtkzV3S2E3RJq5o31KPk1M+yqEntXA=="; // data.go.kr 인증키
	  
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = preProcessing(request, response);
		switch (action) {

		// 회원가입 폼 가기
		case "regist-member-form" -> forward(request, response, "/member/member-regist-form.jsp");
		// 회원가입 요청
		case "regist-member" -> registMember(request, response);
		// 로그인 폼 요청
		case "login-member-form" -> forward(request, response, "/member/member-login-form.jsp");
		// 로그인 요청
		case "login-member" -> loginMember(request, response);
		// 마이 페이지 요청
		case "mypage-member-form" -> forward(request, response, "/member/member-mypage-form.jsp");
		// 회원 수정
		case "modify-member" -> modifyMember(request, response);
		// 회원 탈퇴
		case "delete-member" -> deleteMember(request, response);
		// 로그아웃
		case "logout-member" -> logoutMember(request, response);
		// 비밀번호 찾기
		case "selet-member" -> seletMember(request, response);
		// 맴버 리스트 가져오기
		case "member-list" -> memberList(request, response);
		// 관리자 회원 디테일 접속 
		case "member-detail" -> memberDetail(request, response);
		// 관리자가 회원 삭제 하기 ->
		case "delete-member-admin" -> deleteMemberAdmin(request, response);
		
		default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 처리
		}
	}
	private void memberDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
        	String id = request.getParameter("id");
            Member member = mService.selectDetail(id);
            request.setAttribute("member", member);
            request.setAttribute("key_vworld", keyVworld);
            request.setAttribute("key_sgis_service_id", keySgisServiceId);
            request.setAttribute("key_sgis_security", keySgisSecurity);
            request.setAttribute("key_data", keyData);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("alertMsg", e.getMessage());
        }
        forward(request, response, "/member/member-detail.jsp");
    }
	
	private void memberList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> keyMap = Map.of("1", "name", "2", "id");
        String key = request.getParameter("key");
        if (key != null) {
            key = keyMap.getOrDefault(key, "");
        }
        String word = "";
        word = request.getParameter("word");
        System.out.println("membbeList : "+request.getParameter("currentPage"));
        String currentPageStr = request.getParameter("currentPage");
        int currentPage = 1;
        if(currentPageStr!=null) {
        	currentPage=Integer.parseInt(currentPageStr);
        }
        
        try {
            Page<Member> page = mService.search(new SearchCondition(key, word, currentPage));
            request.setAttribute("page", page);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("alertMsg", e.getMessage());
        }
        forward(request, response, "/member/member-list.jsp");
    }
	
	private void seletMember(HttpServletRequest request, HttpServletResponse response)  throws SecurityException, IOException, ServletException{
		try {
			// 찾아봐
			String id = request.getParameter("id");
			String tel = request.getParameter("tel");

			String msg = mService.find(id, tel);
			if (msg == null) {
				// 찾기 실패
				msg = "비밀번호 못찾았습니다 실패 되었습니다.";
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				forward(request, response, "/member/member-password-select.jsp");
			} else {
				// 찾기 성공
				msg = "당신의 비밀번호는? : " + msg ;
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				forward(request, response, "/member/member-login-form.jsp");
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
			forward(request, response, "/member/member-login-form.jsp");
		}
	}

	private void logoutMember(HttpServletRequest request, HttpServletResponse response)
			throws SecurityException, IOException, ServletException {
		try {
			request.getSession().invalidate();
			redirect(request, response, "/");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
		}
	}

	private void deleteMember(HttpServletRequest request, HttpServletResponse response)
			throws SecurityException, IOException, ServletException {
		try {
			int result = -1;
			Member member = (Member) request.getSession().getAttribute("member");
			String id = member.getId();
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");
			if(password.equals(password2))  result = mService.deleteMember(id, password);
			String msg = "삭제가 완료 되었습니다.";
			if (result == -1) {
				msg = "삭제 실패 비밀번호를 확인 해주십쇼.";
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				forward(request, response, "/member/member-mypage-form.jsp");
			} else {
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				request.getSession().removeAttribute("member");;
				redirect(request, response, "/");
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
			forward(request, response, "/member/member-mypage-form.jsp");
		}

	}

	private void deleteMemberAdmin(HttpServletRequest request, HttpServletResponse response)
			throws SecurityException, IOException, ServletException {
		try {
			int result = -1;
			String id = request.getParameter("id");
			String password =  request.getParameter("password");
			result = mService.deleteMember(id, password);
			String msg = "삭제가 완료 되었습니다.";
			if (result == 1) {
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				request.setAttribute("currentPage", "1");
				memberList(request, response);
				System.out.println("controller 삭제성공");
			} else {
				msg = "삭제 실패.";
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);

				
				memberList(request, response);
			}
			System.out.println("controller end");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
			request.setAttribute("currentPage", "1");
//			memberList(request, response);
		}

	}
	
	private void modifyMember(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Member member = (Member) request.getSession().getAttribute("member");
			String id = member.getId();
			String name = request.getParameter("name");
			String password = request.getParameter("password");
			String address = request.getParameter("address");
			String tel = request.getParameter("tel");

			mService.modifyMember(id, name, password, address, tel);
			String msg = "수정이 완료 되었습니다.";

			member.setName(name);
			member.setPassword(password);
			member.setAddress(address);
			member.setTel(tel);
			request.getSession().setAttribute("member", member);
			request.setAttribute("alertMsg", msg);
			request.getSession().setAttribute("alertMsg", msg);
			forward(request, response, "/member/member-mypage-form.jsp");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
			forward(request, response, "/member/member-mypage-form.jsp");
		}
	}

	private void loginMember(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 로그인 하기
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			String rememberMe = request.getParameter("remember-me");
			Member member = mService.login(id, password);
			if(rememberMe==null) {
        		setupCookie("rememberMe", id, 0, null, response);
        	}else {
        		setupCookie("rememberMe", id, 60*60*10, null, response);
        	}
			String msg = "로그인이 완료 되었습니다.";
			if (member == null) {
				// 로그인 실패
				msg = "로그인 실패 되었습니다.";
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				forward(request, response, "/member/member-login-form.jsp");
			} else {
				// 로그인 성공
				
				request.getSession().setAttribute("member", member); // 세션에 맴버 넣어주기
				request.setAttribute("alertMsg", msg);
				request.getSession().setAttribute("alertMsg", msg);
				redirect(request, response, "/main?action=index");
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e.getMessage());
			forward(request, response, "/member/member-login-form.jsp");
		}

	}

	private void registMember(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			// 파라미터 추출
			String name = request.getParameter("name");
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			String address = request.getParameter("address");
			String tel = request.getParameter("tel");
			// 회원 등록
			mService.registMember(new Member(name, id, password, address, tel));
			// 등록 성공
			String msg = "등록되었습니다. 로그인 후 사용해주세요.";
			request.setAttribute("alertMsg", msg);
			// forward(request, response, "/");
			request.getSession().setAttribute("alertMsg", msg);
			forward(request, response, "/member/member-login-form.jsp");
			// END
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "아마도 아이디 겹칠걸?";
			request.setAttribute("error", msg);
			forward(request, response, "/member/member-regist-form.jsp");

		}
	}

	private void template(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

}
