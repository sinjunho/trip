package com.ssafy.trip.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.PseudoColumnUsage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dto.Attraction;

public class BasicAttractionDao implements AttractionDao {
    private static BasicAttractionDao dao = new BasicAttractionDao();

    private BasicAttractionDao() {
    }

    public static BasicAttractionDao getDao() {
        return dao;
    }
    
    @Override
    public List<Attraction> getAttractionByAddress(Connection con, String contentTypeName, String areaCode, String siGunGuCode, int offset, int limit) throws SQLException {
        String s = "select no, c.content_type_id, content_id, title, content_type_name, sido_name, gugun_name, first_image1, first_image2, map_level, latitude, longitude, tel, a.addr1 addr, homepage, overview " 
                + "from attractions a " 
                + "join contenttypes c on (a.content_type_id = c.content_type_id) " 
                + "join (select s.sido_code sido, g.gugun_code gugun, s.sido_name sido_name, g.gugun_name gugun_name " 
                + "from sidos s join guguns g on (s.sido_code = g.sido_code)) sg "
                + "on (a.area_code = sg.sido and a.si_gun_gu_code = sg.gugun) "
                + "where c.content_type_name = ? and sido_name = ? and gugun_name = ? " ;
        
        List<Attraction> list = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(s)){
            pstmt.setString(1, contentTypeName);
            pstmt.setString(2, areaCode);
            pstmt.setString(3, siGunGuCode);
            
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                list.add(new Attraction(rs.getInt("no"), rs.getString("content_id"),
                        rs.getString("title"), rs.getString("content_type_name"), rs.getString("sido_name"), 
                        rs.getString("gugun_name"),rs.getString("first_image1"),rs.getString("first_image2"),
                        rs.getInt("map_level"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                        rs.getString("tel"),rs.getString("addr"),rs.getString("homepage"),rs.getString("overview")));
            }
        }
        
        return list;
    }
	
    @Override
    public List<Attraction> getAttractionByAddressWithPaging(Connection con, String contentTypeName, String areaCode, String siGunGuCode, int offset, int limit) throws SQLException {
        String s = "select no, c.content_type_id, content_id, title, content_type_name, sido_name, gugun_name, first_image1, first_image2, map_level, latitude, longitude, tel, a.addr1 addr, homepage, overview " 
                + "from attractions a " 
                + "join contenttypes c on (a.content_type_id = c.content_type_id) " 
                + "join (select s.sido_code sido, g.gugun_code gugun, s.sido_name sido_name, g.gugun_name gugun_name " 
                + "from sidos s join guguns g on (s.sido_code = g.sido_code)) sg "
                + "on (a.area_code = sg.sido and a.si_gun_gu_code = sg.gugun) "
                + "where c.content_type_id = ? and sido = ? and gugun = ? " 
                + "limit ?, ?";
        
        List<Attraction> list = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(s)){
            pstmt.setString(1, contentTypeName);
            pstmt.setString(2, areaCode);
            pstmt.setString(3, siGunGuCode);
            pstmt.setInt(4, offset);
            pstmt.setInt(5, limit);
            
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()) {
                list.add(new Attraction(rs.getInt("no"), rs.getString("content_id"),
                        rs.getString("title"), rs.getString("content_type_name"), rs.getString("sido_name"), 
                        rs.getString("gugun_name"),rs.getString("first_image1"),rs.getString("first_image2"),
                        rs.getInt("map_level"), rs.getFloat("latitude"), rs.getFloat("longitude"),
                        rs.getString("tel"),rs.getString("addr"),rs.getString("homepage"),rs.getString("overview")));
            }
        }
        
        return list;
    }

    
    @Override
    public int getTotalAttractionCount(Connection con, String contentTypeName, String areaCode, String siGunGuCode) throws SQLException {
        String sql = "select count(*) as total " 
                + "from attractions a " 
                + "join contenttypes c on (a.content_type_id = c.content_type_id) " 
                + "join (select s.sido_code sido, g.gugun_code gugun, s.sido_name sido_name, g.gugun_name gugun_name " 
                + "from sidos s join guguns g on (s.sido_code = g.sido_code)) sg "
                + "on (a.area_code = sg.sido and a.si_gun_gu_code = sg.gugun) "
                + "where c.content_type_id = ? and sido = ? and gugun = ?"; 
        
        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, contentTypeName);
            pstmt.setString(2, areaCode);
            pstmt.setString(3, siGunGuCode);
            
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
	public List<Map<Integer, String>> getContent(Connection con) throws SQLException{
		String s = "select * from contenttypes";
		
		List<Map<Integer, String>> list = new ArrayList<>();
		
		try(PreparedStatement pstmt = con.prepareStatement(s)){
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				HashMap<Integer, String> map = new HashMap<>();
				map.put(rs.getInt("content_type_id"), rs.getString("content_type_name"));
				list.add(map);
			}
		}
		return list;
	}
	
	public List<Map<Integer, String>> getSido(Connection con) throws SQLException{
		String s = "select `sido_code`, `sido_name` from sidos";
		
		List<Map<Integer, String>> list = new ArrayList<>();
		
		try(PreparedStatement pstmt = con.prepareStatement(s)){
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				HashMap<Integer, String> map = new HashMap<>();
				map.put(rs.getInt("sido_code"), rs.getString("sido_name"));
				list.add(map);
			}
		}
		return list;
	}
	
	public List<Map<Integer, String>> getGugun(Connection con, String code) throws SQLException{
		String s = "select gugun_code, gugun_name from sidos s join guguns g on (s.sido_code = g.sido_code) where s.sido_code = ? ";
		
		List<Map<Integer, String>> list = new ArrayList<>();
		
		try(PreparedStatement pstmt = con.prepareStatement(s)){
			pstmt.setString(1, code);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				HashMap<Integer, String> map = new HashMap<>();
				map.put(rs.getInt("gugun_code"), rs.getString("gugun_name"));
				list.add(map);
			}
			
			System.out.println(list.size());
		}
		
		return list;
	}
	
	public Attraction getAttractionByNo(Connection con, int no) throws SQLException {
		String sql = "select no, c.content_type_id , content_id, title, content_type_name, sido_name, gugun_name, first_image1, first_image2, map_level, latitude, longitude, tel, a.addr1 addr, homepage, overview " 
				+ "from attractions a " 
				+ "join contenttypes c on (a.content_type_id = c.content_type_id) " 
				+ "join (select s.sido_code sido, g.gugun_code gugun, s.sido_name sido_name, g.gugun_name gugun_name " 
				+ "from sidos s join guguns g on (s.sido_code = g.sido_code)) sg "
				+ "on (a.area_code = sg.sido and a.si_gun_gu_code = sg.gugun) "
				+ "where no = ?"; 
		try(PreparedStatement pstmt = con.prepareStatement(sql)){
			pstmt.setInt(1, no);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return new Attraction(rs.getInt("no"), rs.getString("content_id"),
						rs.getString("title"), rs.getString("content_type_name"), rs.getString("sido_name"), 
						rs.getString("gugun_name"),rs.getString("first_image1"),rs.getString("first_image2"),
						rs.getInt("map_level"), rs.getFloat("latitude"), rs.getFloat("longitude"),
						rs.getString("tel"),rs.getString("addr"),rs.getString("homepage"),rs.getString("overview"));
			}
		}
		return null;
	}
	
	@Override
	public List<Attraction> getRandomAttractions(Connection con, int count) throws SQLException {
	    String sql = "select no, c.content_type_id, content_id, title, content_type_name, sido_name, gugun_name, " +
	                 "first_image1, first_image2, map_level, latitude, longitude, tel, a.addr1 addr, homepage, overview " + 
	                 "from attractions a " + 
	                 "join contenttypes c on (a.content_type_id = c.content_type_id) " + 
	                 "join (select s.sido_code sido, g.gugun_code gugun, s.sido_name sido_name, g.gugun_name gugun_name " + 
	                 "from sidos s join guguns g on (s.sido_code = g.sido_code)) sg " +
	                 "on (a.area_code = sg.sido and a.si_gun_gu_code = sg.gugun) " +
	                 "where first_image1 is not null and first_image1 != '' " +  
	                 "order by rand() limit ?";
	    
	    List<Attraction> list = new ArrayList<>();
	    try(PreparedStatement pstmt = con.prepareStatement(sql)){
	        pstmt.setInt(1, count);
	        
	        ResultSet rs = pstmt.executeQuery();
	        
	        while(rs.next()) {
	            list.add(new Attraction(rs.getInt("no"), rs.getString("content_id"),
	                    rs.getString("title"), rs.getString("content_type_name"), rs.getString("sido_name"), 
	                    rs.getString("gugun_name"),rs.getString("first_image1"),rs.getString("first_image2"),
	                    rs.getInt("map_level"), rs.getFloat("latitude"), rs.getFloat("longitude"),
	                    rs.getString("tel"),rs.getString("addr"),rs.getString("homepage"),rs.getString("overview")));
	        }
	    }
	    
	    return list;
	}
	
	@Override
    public int updateViewCount(Connection con, int no) throws SQLException {
        String sql = "UPDATE attractions SET view_cnt = view_cnt + 1 WHERE no = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, no);
            return pstmt.executeUpdate();
        }
    }
	
	@Override
    public List<Attraction> allCountView(Connection con) throws SQLException {
        String sql = "SELECT a.no, a.title, s.sido_name, a.view_cnt from attractions a join sidos s on(a.area_code = s.sido_code) where view_cnt >= 1";
        List<Attraction> list = new ArrayList<>();
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        	ResultSet rs =  pstmt.executeQuery();
        	 while(rs.next()) {
                 list.add(new Attraction(rs.getInt("no"),rs.getString("title"), rs.getString("sido_name"), rs.getInt("view_cnt")));
             }
           
        }
		return list;
        
       
    }
	
}
