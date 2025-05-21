package com.ssafy.trip.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.trip.model.dao.AttractionDao;
import com.ssafy.trip.model.dto.Attraction;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 기본 설정
public class BasicAttractionService implements AttractionService {
    
    private final AttractionDao attractionDao;
    
    @Override
    public List<Attraction> getAttractionByAddress(String contentTypeName, String areaCode, String siGunGuCode)
            throws Exception {
        return attractionDao.getAttractionByAddress(contentTypeName, areaCode, siGunGuCode);
    }

    @Override
    public List<Attraction> getAttractionByAddressWithPaging(String contentTypeName, String areaCode,
            String siGunGuCode, int offset, int limit) throws Exception {
        return attractionDao.getAttractionByAddressWithPaging(contentTypeName, areaCode, siGunGuCode, offset, limit);
    }

    @Override
    public int getTotalAttractionCount(String contentTypeName, String areaCode, String siGunGuCode)
            throws Exception {
        return attractionDao.getTotalAttractionCount(contentTypeName, areaCode, siGunGuCode);
    }

    @Override
    public List<Map<String, Object>> getContent() throws Exception {
        // 디버깅 로그 추가
        List<Map<String, Object>> result = attractionDao.getContent();
        
        for(Map<String, Object> map : result) {
        	map.forEach((strKey, strValue)->{
        		System.out.println( strKey +":"+ strValue );
        	});
        };
        
//        System.out.println("getContent 결과 크기: " + result.size());
//        if (!result.isEmpty()) {
//            System.out.println("첫 번째 항목 클래스: " + result.get(0).getClass().getName());
//            Map<Object, Object> firstItem = result.get(0);
//            for (Map.Entry<Object, Object> entry : firstItem.entrySet()) {
//                System.out.println("키 타입: " + entry.getKey().getClass().getName() + ", 값 타입: " + 
//                    (entry.getValue() != null ? entry.getValue().getClass().getName() : "null"));
//            }
//        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getSido() throws Exception {
        // 디버깅 로그 추가
        List<Map<String, Object>> result = attractionDao.getSido();
        System.out.println("getSido 결과 크기: " + result.size());
        if (!result.isEmpty()) {
            System.out.println("첫 번째 항목 클래스: " + result.get(0).getClass().getName());
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getGugun(String code) throws Exception {
        // 디버깅 로그 추가
        System.out.println("getGugun 호출 - code: " + code);
        List<Map<String, Object>> result = attractionDao.getGugun(code);
        System.out.println("getGugun 결과 크기: " + result.size());
        return result;
    }

    @Override
    public Attraction getAttractionByNo(int no) throws Exception {
        return attractionDao.getAttractionByNo(no);
    }

    @Override
    public List<Attraction> getRandomAttractions(int count) throws Exception {
        return attractionDao.getRandomAttractions(count);
    }

    @Override
    @Transactional // 수정 메소드에는 읽기 전용이 아닌 트랜잭션 적용
    public void increaseViewCount(int no) throws Exception {
        attractionDao.updateViewCount(no);
    }

    @Override
    public List<Attraction> getRank() throws Exception {
        List<Attraction> list = attractionDao.allCountView();
        if (list != null && !list.isEmpty()) {
            CountingSort(list);
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
    public Attraction[] sortAttractionListByDistance(List<Attraction> nearAttractionList, Attraction detailAttraction) throws Exception {
        if (nearAttractionList == null || nearAttractionList.isEmpty()) {
            return new Attraction[0];
        }
        
        heapSort(nearAttractionList, detailAttraction, nearAttractionList.size());
        
        int idxMin = Math.min(10, nearAttractionList.size()-1);
        if (idxMin <= 0) {
            return new Attraction[0];
        }
        
        Attraction[] nearAttractionArr = new Attraction[idxMin];
        for(int i=0; i<nearAttractionArr.length; i++) {
            if (i+1 < nearAttractionList.size()) {
                nearAttractionArr[i] = nearAttractionList.get(i+1);
            }
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
        if (idx1 < 0 || idx2 < 0 || idx1 >= nearAttractionList.size() || idx2 >= nearAttractionList.size()) {
            return; // 인덱스 범위 체크
        }
        Attraction tmp = nearAttractionList.get(idx1);
        nearAttractionList.set(idx1, nearAttractionList.get(idx2));
        nearAttractionList.set(idx2, tmp);
    }
}