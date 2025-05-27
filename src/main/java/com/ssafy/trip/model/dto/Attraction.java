package com.ssafy.trip.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attraction {
	private int no;
	private String contentId;
	private String title;
	private String contentTypeName;	// 관광지 
	private String sido;	// 서울
	private String gugun;	// 종로구
	private String firstImage1;
	private String firstImage2;
	private int mapLevel;
	private float latitude;
	private float longitude;
	private String tel;
	private String addr;
	private String homepage;
	private String overview;
	private int viewCnt;
	

	public Attraction(int no, String contentId, String title, String contentTypeName, String sido, String gugun,
			String firstImage1, String firstImage2, int mapLevel, float latitude, float longitude, String tel,
			String addr, String homepage, String overview) {
		super();
		this.no = no;
		this.contentId = contentId;
		this.title = title;
		this.contentTypeName = contentTypeName;
		this.sido = sido;
		this.gugun = gugun;
		this.firstImage1 = firstImage1;
		this.firstImage2 = firstImage2;
		this.mapLevel = mapLevel;
		this.latitude = latitude;
		this.longitude = longitude;
		this.tel = tel;
		this.addr = addr;
		this.homepage = homepage;
		this.overview = overview;
	}

	public Attraction(int no, String title, String sido, int viewCnt) {
		super();
		this.no = no;
		this.title = title;
		this.sido = sido;
		this.viewCnt = viewCnt;
	}


}