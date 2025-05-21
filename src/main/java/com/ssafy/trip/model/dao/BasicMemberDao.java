//package com.ssafy.trip.model.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.ssafy.trip.model.dto.Member;
//import com.ssafy.trip.model.dto.SearchCondition;
//
//public class BasicMemberDao implements MemberDao {
//    private static BasicMemberDao dao = new BasicMemberDao();
//
//    private BasicMemberDao() {
//    }
//
//    public static BasicMemberDao getDao() {
//        return dao;
//    }
//
//    @Override
//    public int insert(Connection con, Member member) throws SQLException {
//        String sql = "insert into member (name, id, password, address, tel) values(?,?,?,?,?)";
//        int result = -1;
//        try(PreparedStatement pstmt = con.prepareStatement(sql)){
//        	pstmt.setString(1, member.getName());
//        	pstmt.setString(2, member.getId());
//        	pstmt.setString(3, member.getPassword());
//        	pstmt.setString(4, member.getAddress());
//        	pstmt.setString(5, member.getTel());
//        	result = pstmt.executeUpdate();
//        }
//        //END
//        return result;
//    }
//
//	@Override
//	public Member login(Connection con, String id, String password) throws SQLException {
//		String sql = "Select * from member where id=? and password=?";
//		try(PreparedStatement pstmt = con.prepareStatement(sql)){
//			pstmt.setString(1, id);
//        	pstmt.setString(2, password);
//        	ResultSet rs = pstmt.executeQuery();
//			if (rs.next()) {
//				return new Member(rs.getInt("mno"), rs.getString("id"), rs.getString("password"), rs.getString("name"), rs.getString("role"),rs.getString("address"),rs.getString("tel"));
//			}
//			return null; // 있으면 네임 없으면 널
//		}
//	}
//
//
//	@Override
//	public void modify(Connection con ,String id, String name, String password, String address, String tel) throws SQLException{
//		String sql = "Update member set name=? , password=?, address=?, tel=? where id =?";
//		try(PreparedStatement pstmt = con.prepareStatement(sql)) {
//			pstmt.setString(1, name);
//			pstmt.setString(2, password);
//			pstmt.setString(3, address);
//			pstmt.setString(4, tel);
//			pstmt.setString(5, id);
//			pstmt.executeUpdate();
//		}	
//	}
//
//	@Override
//	public int delete(Connection con, String id, String password) throws SQLException {
//		String sql = "delete from member where id=? and password=?";
//		int result = -1;
//		try(PreparedStatement pstmt = con.prepareStatement(sql)) {
//			pstmt.setString(1, id);
//			pstmt.setString(2, password);
//			result = pstmt.executeUpdate();
//		}
//		return result;
//	}
//
//	@Override
//	public String find(Connection con, String id, String tel) throws SQLException {
//		String sql = "Select password from member where id=? and tel=?";
//		String result = null;
//		try(PreparedStatement pstmt = con.prepareStatement(sql)){
//			pstmt.setString(1, id);
//        	pstmt.setString(2, tel);
//        	ResultSet rs = pstmt.executeQuery();
//			try {
//				if (rs.next()) {
//					result = rs.getString(1);
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			// 있으면 네임 없으면 널
//		}
//		return result; 
//	}
//
//	@Override
//	public List<Member> search(Connection con) throws SQLException {
//		List<Member> list = new ArrayList<>();
//        String sql = "select * from member order by mno desc";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            ResultSet rset = pstmt.executeQuery();
//            while (rset.next()) {
//                Member member = new Member();
//                member.setMno(rset.getInt("mno"));
//                member.setName(rset.getString("name"));
//                member.setId(rset.getString("id"));
//                list.add(member);
//            }
//        }
//        return list;
//	}
//
//	@Override
//	public List<Member> search(Connection con, SearchCondition condition) throws SQLException {
//		List<Member> members = new ArrayList<>();
//        String sql = null;
//        boolean hasKeyWord = condition.hasKeyword();
//        sql = hasKeyWord
//                ? "select * from member where %s like ?  order by mno desc limit ?,?".formatted(condition.getKey())
//                : "select * from member order by mno desc limit ?,?";
//
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            int idx = 1;
//            if (hasKeyWord) {
//                pstmt.setString(idx++, "%" + condition.getWord() + "%");
//            }
//            pstmt.setInt(idx++, condition.getOffset());
//            pstmt.setInt(idx++, condition.getItemsPerPage());
//
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                Member member = new Member();
//                member.setMno(rs.getInt("mno"));
//                member.setName(rs.getString("name"));
//                member.setId(rs.getString("id"));
//                member.setPassword(rs.getString("password"));
//                member.setRole(rs.getString("role"));
//                member.setTel(rs.getString("tel"));
//                members.add(member);
//            }
//        }
//        return members;
//	}
//
//	@Override
//	public int getTotalCount(Connection con, SearchCondition condition) throws SQLException {
//		int result = 0;
//        String sql = null;
//        boolean hasKeyWord = condition.hasKeyword();
//
//        sql = hasKeyWord ? "select count(*) from member where %s like ?".formatted(condition.getKey())
//                : "select count(*) from member";
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
//	}
//
//	@Override
//    public Member selectDetail(Connection con, String id) throws SQLException {
//        Member member = null;
//        String sql = """
//                select *
//                from member 
//                where id=?""";
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setString(1, id);
//            ResultSet rset = pstmt.executeQuery();
//            while (rset.next()) {
//                int mno = rset.getInt("mno");
//                String name = rset.getString("name");
//                String pass = rset.getString("password");
//                String role = rset.getString("role");
//                String address = rset.getString("address");
//                String tel = rset.getString("tel");       
//                if (member == null) {
//                    member = new Member(mno, id, pass, name, role, address, tel);
//                }}
//        }
//        return member;
//    }
//
//	
//
//}
