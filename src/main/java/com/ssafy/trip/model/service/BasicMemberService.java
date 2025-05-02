package com.ssafy.trip.model.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.trip.model.dao.MemberDao;
import com.ssafy.trip.model.dto.Member;
import com.ssafy.trip.model.dto.Page;
import com.ssafy.trip.model.dto.SearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicMemberService implements MemberService {
    
    private final MemberDao dao;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public int registMember(Member member) {
        // Encode the password before saving
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return dao.insert(member);
    }

    @Override
    public Member login(String id, String password) {
        Member member = dao.selectDetail(id);
        
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return member;
        }
        
        return null;
    }
    
    @Override
    public void modifyMember(String id, String name, String password, String address, String tel) {
        // Encode the new password
        String encodedPassword = passwordEncoder.encode(password);
        dao.modify(id, name, encodedPassword, address, tel);
    }

    @Override
    public int deleteMember(String id, String password) {
        Member member = dao.selectDetail(id);
        
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return dao.delete(id, member.getPassword());
        }
        
        return 0;
    }

    @Override
    public String find(String id, String tel) {
        return dao.find(id, tel);
    }

    @Override
    public List<Member> searchAll() {
        return dao.searchAll();
    }

    @Override
    public Page<Member> search(SearchCondition condition) {
        int totalItems = dao.getTotalCount(condition);
        List<Member> members = dao.search(condition);
        return new Page<>(condition, totalItems, members);
    }

    @Override
    public Member selectDetail(String id) {
        return dao.selectDetail(id);
    }
}


 //기존 서비스
//package com.ssafy.trip.model.service;
//
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import com.ssafy.trip.model.dao.MemberDao;
//import com.ssafy.trip.model.dto.Member;
//import com.ssafy.trip.model.dto.Page;
//import com.ssafy.trip.model.dto.SearchCondition;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class BasicMemberService implements MemberService {
//    private final MemberDao dao;
//  
//    private static String pwSalt = "294c892eefc3c87a529fb28392c924bb";
// 
//   
//    
//    private static byte[] getSalt() {
//    	byte[] bytes = pwSalt.getBytes(StandardCharsets.UTF_8);
//    	return bytes;
//    }
//    
//    
//    private static String getSHA(String pw) {
//    	try {
//			MessageDigest dgst = MessageDigest.getInstance("SHA-256");
//			
//			dgst.update(getSalt());
//			
//			byte[] byteData = pw.getBytes();
//			byte[] hashBytes = dgst.digest(byteData);
//			
//			return bytesToHex(hashBytes);
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//    			
//    }
//    
//    private static String bytesToHex(byte[] bytes) {
//    	StringBuilder hexString = new StringBuilder();
//    	
//    	for(byte b : bytes) {
//    		String hex = Integer.toHexString(0xff & b);
//    		if(hex.length() == 1 ) hexString.append('0');
//    		hexString.append(hex);
//    	}
//    	
//    	return hexString.toString().toUpperCase();
//    }
//    
//    @Override
//    public int registMember(Member member)  {      
//			member.setPassword(getSHA(member.getPassword()));
//			return  dao.insert(member);
//	
//			
//    }
//
//    @Override
//    public Member login(String id, String password)  {
//    		password = getSHA(password);
//    		return dao.login(id, password);    		
//    }
//
//	@Override
//	public void modifyMember(String id, String name, String password, String address, String tel)  {		
//    		password = getSHA(password);
//    		dao.modify(id, name, password, address, tel);
//	}
//
//	@Override
//	public int deleteMember(String id, String password) {
//    		password = getSHA(password);
//    		return dao.delete(id, password); 	
//	}
//
//	@Override
//	public String find(String id, String tel) {
//    		return dao.find(id, tel);    		
//	}
//
//	@Override
//	public List<Member> searchAll()  {
//            return dao.searchAll();
//        
//	}
//
//	 @Override
//	    public Page<Member> search(SearchCondition condition)  {
//	            int totalItems = dao.getTotalCount(condition);
//	            List<Member> members = dao.search( condition);
//	            Page<Member> page = new Page<>(condition, totalItems, members);
//	            return page;
//	        
//	    }
//
//	 @Override
//	    public Member selectDetail(String id)  {
//	            return dao.selectDetail(id);   
//	    }
//	
//
//}
