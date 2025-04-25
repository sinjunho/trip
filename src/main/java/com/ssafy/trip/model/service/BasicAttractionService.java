package com.ssafy.trip.model.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ssafy.trip.model.dao.AttractionDao;
import com.ssafy.trip.model.dao.BasicAttractionDao;
import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.util.DBUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAttractionService implements AttractionService {
	private AttractionDao dao = BasicAttractionDao.getDao();
	private DBUtil util = DBUtil.getUtil();
	private static BasicAttractionService service = new BasicAttractionService();


	public static BasicAttractionService getService() {
		return service;
	}

	@Override
	public List<Attraction> getAttractionByAddress(String contentTypeName, String areaCode, String siGunGuCode)
			throws SQLException {
		Connection con = util.getConnection();
		try {
			con.setAutoCommit(false);
			List<Attraction> result = dao.getAttractionByAddress(con, contentTypeName, areaCode, siGunGuCode, 0, 0);
			con.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public List<Attraction> getAttractionByAddressWithPaging(String contentTypeName, String areaCode,
			String siGunGuCode, int offset, int limit) throws SQLException {
		Connection con = util.getConnection();
		try {
			con.setAutoCommit(false);
			List<Attraction> result = dao.getAttractionByAddressWithPaging(con, contentTypeName, areaCode, siGunGuCode,
					offset, limit);
			con.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public int getTotalAttractionCount(String contentTypeName, String areaCode, String siGunGuCode)
			throws SQLException {
		Connection con = util.getConnection();
		try {
			return dao.getTotalAttractionCount(con, contentTypeName, areaCode, siGunGuCode);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
	}

	public List<Map<Integer, String>> getContent() throws SQLException {
		Connection con = util.getConnection();
		try {
			List<Map<Integer, String>> result = dao.getContent(con);

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
	}

	public List<Map<Integer, String>> getSido() throws SQLException {
		Connection con = util.getConnection();

		try {
			List<Map<Integer, String>> result = dao.getSido(con);

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
	}

	public List<Map<Integer, String>> getGugun(String code) throws SQLException {
		Connection con = util.getConnection();
		try {
			List<Map<Integer, String>> result = dao.getGugun(con, code);

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public Attraction getAttractionByNo(int no) throws SQLException {
		Connection con = util.getConnection();
		try {
			return dao.getAttractionByNo(con, no);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public List<Attraction> getRandomAttractions(int count) throws SQLException {
		Connection con = util.getConnection();
		try {
			return dao.getRandomAttractions(con, count);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public void increaseViewCount(int no) throws SQLException {
		Connection con = util.getConnection();
		try {
			con.setAutoCommit(false);
			dao.updateViewCount(con, no);
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public List<Attraction> getRank() throws SQLException {
		Connection con = util.getConnection();
		List<Attraction> list = new ArrayList<>();
		try {
			list = dao.allCountView(con);
			CountingSort(list);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			util.close(con);
		}
		return list;
	}

	private void CountingSort(List<Attraction> list) {
	    if (list == null || list.isEmpty()) {
	        return; // 빈 리스트는 정렬할 필요 X
	    }
	    
	    int maxCount = 0;
	    for (Attraction attraction : list) {
	        maxCount = Math.max(maxCount, attraction.getViewCnt());
	    }
	    
	    int[] countArray = new int[maxCount + 1];
	    for (Attraction attraction : list) {
	        countArray[attraction.getViewCnt()]++;
	    }
	    
	    for (int i = 1; i <= maxCount; i++) {
	        countArray[i] += countArray[i - 1];
	    }
	    
	
	    Attraction[] sortedArray = new Attraction[list.size()];
	    
	    for (int i = list.size() - 1; i >= 0; i--) {
	        Attraction attraction = list.get(i);
	        int cnt = attraction.getViewCnt();
	        sortedArray[countArray[cnt] - 1] = attraction;
	        countArray[cnt]--;
	    }
	    
	    list.clear();
	    
	    int count = 0;
	    int maxElements = Math.min(10, sortedArray.length); // 10개 이하
	    
	    for (int i = sortedArray.length - 1; i >= 0 && count < maxElements; i--) {
	        list.add(sortedArray[i]);
	        count++;
	    }
	    
	}

	@Override
	public Attraction[] sortAttractionListByDistance(List<Attraction> nearAttractionList, Attraction detailAttraction) throws SQLException {
		heapSort(nearAttractionList, detailAttraction, nearAttractionList.size());
		
		if(nearAttractionList == null) {
			return null;
		}
		int idxMin = Math.min(10, nearAttractionList.size()-1);
		Attraction[] nearAttractionArr = new Attraction[idxMin];
		for(int i=0;i<nearAttractionArr.length;i++) {
			nearAttractionArr[i] = nearAttractionList.get(i+1);
		}
		return nearAttractionArr;
	}
	
	public double calculateDistanceByHaversine(double x1, double y1, double x2, double y2) {
		double distance;
		double radius = 6371;
		double toRadian = Math.PI/180;
		
		double deltaLatitude = Math.abs(x1 - x2) * toRadian;
	    double deltaLongitude = Math.abs(y1 - y2) * toRadian;

	    double sinDeltaLat = Math.sin(deltaLatitude / 2);
	    double sinDeltaLng = Math.sin(deltaLongitude / 2);
	    double squareRoot = Math.sqrt(
	        sinDeltaLat * sinDeltaLat +
	        Math.cos(x1 * toRadian) * Math.cos(x2 * toRadian) * sinDeltaLng * sinDeltaLng);

	    distance = 2 * radius * Math.asin(squareRoot);

	    return distance;
	}
	
	public void heapSort(List<Attraction> nearAttractionList, Attraction detailAttraction, int n) {
		//힙구조로 구성 (Build Max-heap) 
        heapify(nearAttractionList, detailAttraction, n);
        
        //루트 제거하고 마지막 요소를 루트로 옮김 (Extract-Max)
        for(int i=n-1; i>=0; i--) {
            swap(nearAttractionList, 0, i);
            
            heapify(nearAttractionList, detailAttraction, i);
        }
	}
	
	//Build Max-heap 
    public void heapify(List<Attraction> nearAttractionList, Attraction detailAttraction, int last) {    //last변수는 가장 마지막 인덱스를 뜻함
        for(int i=1; i<last; i++) {
            int child = i;
            
            while(child>0) {    
                int parent = (child - 1)/2;
                double childResult = calculateDistanceByHaversine(nearAttractionList.get(child).getLatitude(), nearAttractionList.get(child).getLongitude(), detailAttraction.getLatitude(), detailAttraction.getLongitude());
                double parentResult = calculateDistanceByHaversine(nearAttractionList.get(parent).getLatitude(), nearAttractionList.get(parent).getLongitude(), detailAttraction.getLatitude(), detailAttraction.getLongitude());
                if(childResult > parentResult) {     //부모와 비교해서 자식이 클경우엔
                    swap(nearAttractionList, child, parent);    //swap
                }
                child = parent;        
            }
        }
    }
    
    //배열의 요소 두개의 위치를 바꿈 
    static void swap(List<Attraction> nearAttractionList, int idx1, int idx2) {
        Attraction tmp = nearAttractionList.get(idx1);
        nearAttractionList.set(idx1, nearAttractionList.get(idx2));
        nearAttractionList.set(idx2, tmp);
//        arr[idx1]=arr[idx2];    //idx1 : idx1 -> idx2
//        arr[idx2]=tmp;            //idx2 : idx2 -> idx1
    }
}
