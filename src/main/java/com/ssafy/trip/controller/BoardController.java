package com.ssafy.trip.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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

@WebServlet("/board")
public class BoardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BoardService bService = BasicBoardService.getService();

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "write-form" -> forward(request, response, "/board/board-write-form.jsp");
            case "write" -> writeBoard(request, response);
            case "list" -> searchBy(request, response);
            case "detail" -> boardDetail(request, response);
            case "modify-form" -> prepareModifyForm(request, response);
            case "modify" -> modifyBoard(request, response);
            case "delete" -> deleteBoard(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void prepareModifyForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bno = Integer.parseInt(request.getParameter("bno"));
            Board board = bService.selectDetail(bno);
            request.setAttribute("board", board);
            forward(request, response, "/board/board-modify-form.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-list.jsp");
        }
    }

    
    private void writeBoard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            Member member = (Member) request.getSession().getAttribute("member");
            String writer = member != null ? member.getName() : "알 수 없음";
            System.out.println(writer);
            bService.writeBoard(new Board(title, content, writer));
            redirect(request, response, "/board?action=list");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-write-form.jsp");
        }
    }

    private void boardList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String key = request.getParameter("key");
        String word = request.getParameter("word");
        String currentPageStr = request.getParameter("currentPage");
        
        int currentPage = 1;
        if(currentPageStr!=null) {
        	currentPage=Integer.parseInt(currentPageStr);
        }
        try {
            Page<Board> page = bService.search(new SearchCondition(key, word, currentPage));
            request.getSession().setAttribute("page", page);
            forward(request, response, "/board/board-list.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-list.jsp");
        }
    }

    private void boardDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bno = Integer.parseInt(request.getParameter("bno"));
            
            HttpSession session = request.getSession();
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
            request.setAttribute("board", board);
            forward(request, response, "/board/board-detail.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-list.jsp");
        }
    }

    private void modifyBoard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bno = Integer.parseInt(request.getParameter("bno"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            bService.modifyBoard(new Board(bno, title, content, null, null, 0));
            redirect(request, response, "/board?action=list");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-modify-form.jsp");
        }
    }

    private void deleteBoard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bno = Integer.parseInt(request.getParameter("bno"));
            bService.deleteBoard(bno);
            redirect(request, response, "/board?action=list");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-list.jsp");
        }
    }
    private void searchBy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	 String key = request.getParameter("key");
         
    	 if(key == null) boardList(request, response); 
    	 
    	 else if(key.equals("1")) searchByTitle(request, response); 
         else if(key.equals("2")) searchByWriter(request, response); 
         else boardList(request, response);
         
    }
    private void searchByTitle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String key = request.getParameter("key");
        String word = request.getParameter("word");
        String currentPageStr = request.getParameter("currentPage");
        int currentPage = 1;
        if(currentPageStr!=null) {
        	currentPage=Integer.parseInt(currentPageStr);
        }
        try {
            Page<Board> page = bService.searchByTitle(new SearchCondition(key, word, currentPage));
            request.getSession().setAttribute("page", page);
            forward(request, response, "/board/board-list.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-list.jsp");
        }
    }
    
    
    private void searchByWriter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String key = request.getParameter("key");
        String word = request.getParameter("word");
        String currentPageStr = request.getParameter("currentPage");
        int currentPage = 1;
        if(currentPageStr!=null) {
        	currentPage=Integer.parseInt(currentPageStr);
        }
        try {
            Page<Board> page = bService.searchByWriter(new SearchCondition(key, word, currentPage));
            request.getSession().setAttribute("page", page);
            forward(request, response, "/board/board-list.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/board/board-list.jsp");
        }
    }
    
    private void forward(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
