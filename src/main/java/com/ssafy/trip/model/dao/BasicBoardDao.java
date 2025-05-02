//package com.ssafy.trip.model.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.ssafy.trip.model.dto.Board;
//import com.ssafy.trip.model.dto.SearchCondition;
//
//public class BasicBoardDao implements BoardDao {
//    private static BasicBoardDao dao = new BasicBoardDao();
//
//    private BasicBoardDao() {}
//
//    public static BasicBoardDao getDao() {
//        return dao;
//    }
//
//    @Override
//    public int insert(Connection con, Board board) throws SQLException {
//        String sql = "INSERT INTO board (title, content, writer) VALUES (?, ?, ?)";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setString(1, board.getTitle());
//            pstmt.setString(2, board.getContent());
//            pstmt.setString(3, board.getWriter());
//            return pstmt.executeUpdate();
//        }
//    }
//
//    @Override
//    public List<Board> search(Connection con, SearchCondition condition) throws SQLException {
//        List<Board> boards = new ArrayList<>();
//        String sql = "SELECT * FROM board ORDER BY bno DESC LIMIT ?, ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, condition.getOffset());
//            pstmt.setInt(2, condition.getItemsPerPage());
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                boards.add(new Board(
//                    rs.getInt("bno"),
//                    rs.getString("title"),
//                    rs.getString("content"),
//                    rs.getString("writer"),
//                    rs.getTimestamp("reg_date"),
//                    rs.getInt("view_cnt")
//                ));
//            }
//        }
//        return boards;
//    }
//
//    @Override
//    public Board selectDetail(Connection con, int bno) throws SQLException {
//        String sql = "SELECT * FROM board WHERE bno = ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, bno);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return new Board(
//                    rs.getInt("bno"),
//                    rs.getString("title"),
//                    rs.getString("content"),
//                    rs.getString("writer"),
//                    rs.getTimestamp("reg_date"),
//                    rs.getInt("view_cnt")
//                );
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public int update(Connection con, Board board) throws SQLException {
//        String sql = "UPDATE board SET title = ?, content = ? WHERE bno = ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setString(1, board.getTitle());
//            pstmt.setString(2, board.getContent());
//            pstmt.setInt(3, board.getBno());
//            return pstmt.executeUpdate();
//        }
//    }
//
//    @Override
//    public int delete(Connection con, int bno) throws SQLException {
//        String sql = "DELETE FROM board WHERE bno = ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, bno);
//            return pstmt.executeUpdate();
//        }
//    }
//
//    @Override
//    public int getTotalCount(Connection con, SearchCondition condition) throws SQLException {
//        int result = 0;
//        String sql;
//        boolean hasKeyWord = condition.hasKeyword();
//
//        sql = hasKeyWord ? "SELECT COUNT(*) FROM board WHERE " + condition.getKey() + " LIKE ?"
//                         : "SELECT COUNT(*) FROM board";
//
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            if (hasKeyWord) {
//                pstmt.setString(1, "%" + condition.getWord() + "%");
//            }
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                result = rs.getInt(1);
//            }
//        }
//        return result;
//    }
//
//    @Override
//    public int updateViewCount(Connection con, int bno) throws SQLException {
//        String sql = "UPDATE board SET view_cnt = view_cnt + 1 WHERE bno = ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, bno);
//            return pstmt.executeUpdate();
//        }
//    }
//    
//    public List<Board> searchByTitle(Connection con, SearchCondition condition) throws SQLException {
//        List<Board> boards = new ArrayList<>();
//        String sql = "select * from board where title = ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setString(1, condition.getWord());
//           
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                boards.add(new Board(
//                    rs.getInt("bno"),
//                    rs.getString("title"),
//                    rs.getString("content"),
//                    rs.getString("writer"),
//                    rs.getTimestamp("reg_date"),
//                    rs.getInt("view_cnt")
//                ));
//            }
//        }
//        return boards;
//    }
//    
//    public List<Board> searchByWriter(Connection con, SearchCondition condition) throws SQLException {
//        List<Board> boards = new ArrayList<>();
//        String sql = "select * from board where writer = ?";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setString(1, condition.getWord());
//           
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                boards.add(new Board(
//                    rs.getInt("bno"),
//                    rs.getString("title"),
//                    rs.getString("content"),
//                    rs.getString("writer"),
//                    rs.getTimestamp("reg_date"),
//                    rs.getInt("view_cnt")
//                ));
//            }
//        }
//        return boards;
//    }
//}
