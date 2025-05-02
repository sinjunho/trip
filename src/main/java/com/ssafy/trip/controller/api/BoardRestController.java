//package com.ssafy.trip.controller.api;
//
//import java.sql.SQLException;
//import java.util.Map;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ssafy.trip.model.dto.Board;
//import com.ssafy.trip.model.dto.Member;
//import com.ssafy.trip.model.dto.Page;
//import com.ssafy.trip.model.dto.SearchCondition;
//import com.ssafy.trip.model.service.BoardService;
//
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/boards")
//@RequiredArgsConstructor
//public class BoardRestController {
//    
//    private final BoardService boardService;
//    
//    @GetMapping
//    public ResponseEntity<?> getBoardList(
//            @RequestParam(required = false) String key,
//            @RequestParam(required = false) String word,
//            @RequestParam(defaultValue = "1") int currentPage) {
//        try {
//            SearchCondition condition = new SearchCondition(key, word, currentPage);
//            Page<Board> page = boardService.search(condition);
//            
//            return ResponseEntity.ok(page);
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("message", "Error retrieving boards: " + e.getMessage()));
//        }
//    }
//    
//    @GetMapping("/{bno}")
//    public ResponseEntity<?> getBoardDetail(@PathVariable int bno) {
//        try {
//            Board board = boardService.selectDetail(bno);
//            if (board == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "Board not found"));
//            }
//            
//            // Increase view count
//            boardService.increaseViewCount(bno);
//            
//            return ResponseEntity.ok(board);
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("message", "Error retrieving board: " + e.getMessage()));
//        }
//    }
//    
//    @PostMapping
//    public ResponseEntity<?> createBoard(@RequestBody Board board, HttpSession session) {
//        try {
//            // Check if user is logged in
//            Member member = (Member) session.getAttribute("member");
//            if (member == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("message", "You must be logged in to create a board"));
//            }
//            
//            // Set the writer from the logged in user
//            board.setWriter(member.getName());
//            
//            boardService.writeBoard(board);
//            
//            return ResponseEntity.status(HttpStatus.CREATED)
//                .body(Map.of("message", "Board created successfully"));
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("message", "Error creating board: " + e.getMessage()));
//        }
//    }
//    
//    @PutMapping("/{bno}")
//    public ResponseEntity<?> updateBoard(
//            @PathVariable int bno,
//            @RequestBody Board board,
//            HttpSession session) {
//        try {
//            // Check if board exists
//            Board existingBoard = boardService.selectDetail(bno);
//            if (existingBoard == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "Board not found"));
//            }
//            
//            // Check if user is authorized
//            Member member = (Member) session.getAttribute("member");
//            if (member == null || (!member.getName().equals(existingBoard.getWriter()) && 
//                !member.getRole().equals("admin"))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("message", "You are not authorized to update this board"));
//            }
//            
//            // Set the board number and update
//            board.setBno(bno);
//            boardService.modifyBoard(board);
//            
//            return ResponseEntity.ok(Map.of("message", "Board updated successfully"));
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("message", "Error updating board: " + e.getMessage()));
//        }
//    }
//    
//    @DeleteMapping("/{bno}")
//    public ResponseEntity<?> deleteBoard(@PathVariable int bno, HttpSession session) {
//        try {
//            // Check if board exists
//            Board existingBoard = boardService.selectDetail(bno);
//            if (existingBoard == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "Board not found"));
//            }
//            
//            // Check if user is authorized
//            Member member = (Member) session.getAttribute("member");
//            if (member == null || (!member.getName().equals(existingBoard.getWriter()) && 
//                !member.getRole().equals("admin"))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("message", "You are not authorized to delete this board"));
//            }
//            
//            boardService.deleteBoard(bno);
//            
//            return ResponseEntity.ok(Map.of("message", "Board deleted successfully"));
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("message", "Error deleting board: " + e.getMessage()));
//        }
//    }
//}