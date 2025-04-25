package com.ssafy.trip.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ssafy.trip.model.dto.Board;
import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.model.service.BasicBoardService;
import com.ssafy.trip.model.service.BoardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final BoardService bService = BasicBoardService.getService();

//    protected void service(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String action = request.getParameter("action");
//        switch (action) {
//            case "write-form" -> forward(request, response, "/board/board-write-form.jsp");
//            case "write" -> writeBoard(request, response);
//            case "list" -> searchBy(request, response);
//            case "detail" -> boardDetail(request, response);
//            case "modify-form" -> prepareModifyForm(request, response);
//            case "modify" -> modifyBoard(request, response);
//            case "delete" -> deleteBoard(request, response);
//            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
//        }
//    }

	@GetMapping("/modify-form")
	private String prepareModifyForm(@RequestParam int bno, Model model) {
		try {
			Board board = bService.selectDetail(bno);
			model.addAttribute("board", board);
			return "board/board-modify-form";
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "board/board-list";
		}
	}

	@GetMapping("/write-form")
	private String writeForm() {
		return "board/board-write-form";

	}

	@PostMapping("/write")
	private String writeBoard(@RequestParam String title,@RequestParam Member member, @RequestParam String content, Model model,
			HttpSession session) {
		try {
			//Member member = (Member) session.getAttribute("member");
			model.addAttribute(member);
			session.setAttribute("member", member);
			String writer = member != null ? member.getName() : "알 수 없음";
			System.out.println(writer);
			bService.writeBoard(new Board(title, content, writer));
			return "redirect:/board/list";
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "board/board-write-form";
		}
	}

	@GetMapping("/list-page")
	private String boardList(@RequestParam String key, @RequestParam String word,
			@RequestParam(defaultValue = "1") int currentPage, HttpSession session, Model model) {
//        String key = request.getParameter("key");
//        String word = request.getParameter("word");
//        String currentPageStr = request.getParameter("currentPage");

//        int currentPage = 1;
//        if(currentPageStr!=null) {
//        	currentPage=Integer.parseInt(currentPageStr);
//        }
		try {
			Page<Board> page = bService.search(new SearchCondition(key, word, currentPage));
			session.setAttribute("page", page);
			return "board/board-list";
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "board/board-list";
		}
	}

	@GetMapping("/detail")
	private String boardDetail(@RequestParam int bno, HttpSession session, Model model) {
		try {

			Set<Integer> readArticles = (Set<Integer>) session.getAttribute("readArticles");

			if (readArticles == null) {
				readArticles = new HashSet<>();
				session.setAttribute("readArticles", readArticles);
			}

			if (!readArticles.contains(bno)) {
				bService.increaseViewCount(bno);
				readArticles.add(bno);
			}

			Board board = bService.selectDetail(bno);
			model.addAttribute("board", board);
			return "/board/board-detail";
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "/board/board-list";
		}
	}

	@PostMapping("/modify")
	private String modifyBoard(@RequestParam int bno, @RequestParam String title, @RequestParam String content,
			Model model) {
		try {
			bService.modifyBoard(new Board(bno, title, content, null, null, 0));
			return "redirect:/board/list";
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "board/board-modify-form";
		}
	}

	@PostMapping("/delete")
	private String deleteBoard(@RequestParam int bno, Model model) {
		try {
			bService.deleteBoard(bno);
			return "redirect:/board/list";
		} catch (SQLException e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "board/board-list";
		}
	}

	@GetMapping("/list")
	private String searchBy(@RequestParam(required = false) String key, @RequestParam(required = false) String word,
			@RequestParam(defaultValue = "1") int currentPage, Model model, HttpSession session) {

		try {
			Page<Board> page;

			if (key == null) {
				page = bService.search(new SearchCondition(key, word, currentPage));
			} else if (key.equals("1")) {
				page = bService.searchByTitle(new SearchCondition(key, word, currentPage));
			} else if (key.equals("2")) {
				page = bService.searchByWriter(new SearchCondition(key, word, currentPage));
			} else {
				page = bService.search(new SearchCondition(key, word, currentPage));
			}

			session.setAttribute("page", page);
			return "board/board-list";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "board/board-list";
		}

//    	 String key = request.getParameter("key");

//    	 if(key == null) boardList(request, response); 
//    	 else if(key.equals("1")) searchByTitle(request, response); 
//         else if(key.equals("2")) searchByWriter(request, response); 
//         else boardList(request, response);

//    	 if(key == null)  return "board/list-page"; 
//    	 else if(key.equals("1")) return "board/list-by-title"; 
//    	 else if(key.equals("2")) return "board/list-by-writer"; 
//    	 else return "board/list-page";
	}

//    @GetMapping("/list-by-title")
//    private String searchByTitle(@RequestParam(required = false) String key, @RequestParam(required = false) String word, 
//    		@RequestParam(defaultValue = "1") int currentPage, HttpSession session, Model model ) {
////        String key = request.getParameter("key");
////        String word = request.getParameter("word");
////        String currentPageStr = request.getParameter("currentPage");
////        int currentPage = 1;
////        if(currentPageStr!=null) {
////        	currentPage=Integer.parseInt(currentPageStr);
////        }
//        try {
//            Page<Board> page = bService.searchByTitle(new SearchCondition(key, word, currentPage));
//            session.setAttribute("page", page);
//            return "board/board-list";
//        } catch (SQLException e) {
//            e.printStackTrace();
//            model.addAttribute("error", e.getMessage());
//            return "board/board-list";
//        }
//    }
//    
//    @GetMapping("/list-by-writer")
//    private String searchByWriter(@RequestParam(required = false) String key, @RequestParam(required = false) String word, 
//    		@RequestParam(defaultValue = "1") int currentPage, HttpSession session, Model model) {
////        String key = request.getParameter("key");
////        String word = request.getParameter("word");
////        String currentPageStr = request.getParameter("currentPage");
////        int currentPage = 1;
////        if(currentPageStr!=null) {
////        	currentPage=Integer.parseInt(currentPageStr);
////        }
//        try {
//            Page<Board> page = bService.searchByWriter(new SearchCondition(key, word, currentPage));
//            session.setAttribute("page", page);
//            return "board/board-list";
//        } catch (SQLException e) {
//            e.printStackTrace();
//            model.addAttribute("error", e.getMessage());
//            return "board/board-list";
//        }
//    }
}
