package com.ssafy.trip.model.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.trip.model.dao.BasicBoardDao;
import com.ssafy.trip.model.dao.BoardDao;
import com.ssafy.trip.model.dto.Board;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.util.DBUtil;

public class BasicBoardService implements BoardService {
    private BoardDao dao = BasicBoardDao.getDao();
    private DBUtil util = DBUtil.getUtil();
    private static BasicBoardService service = new BasicBoardService();

    private BasicBoardService() {
    }

    public static BasicBoardService getService() {
        return service;
    }

    @Override
    public void writeBoard(Board board) throws SQLException {
        Connection con = util.getConnection();
        try {
            con.setAutoCommit(false);
            dao.insert(con, board);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            util.close(con);
        }
    }

    @Override
    public Page<Board> search(SearchCondition condition) throws SQLException {
        try (Connection con = util.getConnection()) {
            int totalItems = dao.getTotalCount(con, condition);
            List<Board> boards = dao.search(con, condition);
            return new Page<>(condition, totalItems, boards);
        }
    }

    @Override
    public Board selectDetail(int bno) throws SQLException {
        try (Connection con = util.getConnection()) {
            return dao.selectDetail(con, bno);
        }
    }

    @Override
    public void modifyBoard(Board board) throws SQLException {
        Connection con = util.getConnection();
        try {
            con.setAutoCommit(false);
            dao.update(con, board);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            util.close(con);
        }
    }

    @Override
    public void deleteBoard(int bno) throws SQLException {
        Connection con = util.getConnection();
        try {
            con.setAutoCommit(false);
            dao.delete(con, bno);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            util.close(con);
        }
    }
    
    @Override
    public void increaseViewCount(int bno) throws SQLException {
        Connection con = util.getConnection();
        try {
            con.setAutoCommit(false);
            dao.updateViewCount(con, bno);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            util.close(con);
        }
    }
    
    @Override
    public Page<Board> searchByTitle(SearchCondition condition) throws SQLException {
        try (Connection con = util.getConnection()) {
            int totalItems = dao.getTotalCount(con, condition);
            List<Board> boards = dao.searchByTitle(con, condition);
            return new Page<>(condition, totalItems, boards);
        }
    }
    
    @Override
    public Page<Board> searchByWriter(SearchCondition condition) throws SQLException {
        try (Connection con = util.getConnection()) {
            int totalItems = dao.getTotalCount(con, condition);
            List<Board> boards = dao.searchByWriter(con, condition);
            return new Page<>(condition, totalItems, boards);
        }
    }
}
