package com.ssafy.trip.model.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.trip.model.dao.BasicMemberDao;
import com.ssafy.trip.model.dao.MemberDao;
import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;
import com.ssafy.trip.util.DBUtil;

public class BasicMemberService implements MemberService {
    private MemberDao dao = BasicMemberDao.getDao();
    private DBUtil util = DBUtil.getUtil();
    private static BasicMemberService service = new BasicMemberService();
    private static String pwSalt = "294c892eefc3c87a529fb28392c924bb";
//    
    private BasicMemberService() {
    }

    public static BasicMemberService getService() {
        return service;
    }
    
    private static byte[] getSalt() {
    	byte[] bytes = pwSalt.getBytes(StandardCharsets.UTF_8);
    	return bytes;
    }
    private static String getSHA(String pw) {
    	try {
			MessageDigest dgst = MessageDigest.getInstance("SHA-256");
			
			dgst.update(getSalt());
			
			byte[] byteData = pw.getBytes();
			byte[] hashBytes = dgst.digest(byteData);
			
			return bytesToHex(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    			
    }
    private static String bytesToHex(byte[] bytes) {
    	StringBuilder hexString = new StringBuilder();
    	
    	for(byte b : bytes) {
    		String hex = Integer.toHexString(0xff & b);
    		if(hex.length() == 1 ) hexString.append('0');
    		hexString.append(hex);
    	}
    	
    	return hexString.toString().toUpperCase();
    }
    @Override
    public int registMember(Member member) throws SQLException {
    	Connection con = util.getConnection();
    	try {
			con.setAutoCommit(false);
			member.setPassword(getSHA(member.getPassword()));
			int result = dao.insert(con, member);
			con.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
			throw e;
		} finally {
			util.close(con);
		}
        //END
    }

    @Override
    public Member login(String id, String password) throws SQLException {
    	Connection con = util.getConnection();
    	try {
    		con.setAutoCommit(false);
    		Member result = null;
    		password = getSHA(password);
    		result = dao.login(con, id, password);    		
    		con.commit();
    		return result;
    	} catch(SQLException e){
    		e.printStackTrace();
			con.rollback();
			throw e;
    	} finally {
    		util.close(con);
		}

    }

	@Override
	public void modifyMember(String id, String name, String password, String address, String tel) throws SQLException {
		Connection con = util.getConnection();
    	try {
    		con.setAutoCommit(false);
    		password = getSHA(password);
    		dao.modify(con, id, name, password, address, tel);
    		con.commit();
    	} catch(SQLException e){
    		e.printStackTrace();
			con.rollback();
			throw e;
    	} finally {
    		util.close(con);
		}
		
	}

	@Override
	public int deleteMember(String id, String password) throws SQLException {
		Connection con = util.getConnection();
		int result = -1;
    	try {
    		con.setAutoCommit(false);
    		// TODO: 01: 주소도 지워주기
    		password = getSHA(password);
    		result = dao.delete(con, id, password);
    		con.commit();
    	} catch(SQLException e){
    		e.printStackTrace();
			con.rollback();
			throw e;
    	} finally {
    		util.close(con);
		}
		return result;
	}

	@Override
	public String find(String id, String tel) throws SQLException {
		Connection con = util.getConnection();
		String result = null;
		try {
    		con.setAutoCommit(false);
    		result = dao.find(con, id, tel);    		
    		con.commit();
    		return result;
    	} catch(SQLException e){
    		e.printStackTrace();
			con.rollback();
			throw e;
    	} finally {
    		util.close(con);
		}
	}

	@Override
	public List<Member> search() throws SQLException {
		try (Connection con = util.getConnection()) {
            return dao.search(con);
        }
	}

	 @Override
	    public Page<Member> search(SearchCondition condition) throws SQLException {
	        try (Connection con = util.getConnection()) {
	            int totalItems = dao.getTotalCount(con, condition);
	            List<Member> members = dao.search(con, condition);
	            Page<Member> page = new Page<>(condition, totalItems, members);
	            return page;
	        }
	    }

	 @Override
	    public Member selectDetail(String id) throws SQLException {
	        try (Connection con = util.getConnection()) {
	            return dao.selectDetail(con, id);
	        }
	    }
	

}
