package com.ssafy.trip.model.dto;

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






	public int getViewCnt() {
		return viewCnt;
	}






	public void setViewCnt(int viewCnt) {
		this.viewCnt = viewCnt;
	}






	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContentTypeName() {
		return contentTypeName;
	}

	public void setContentTypeName(String contentTypeName) {
		this.contentTypeName = contentTypeName;
	}

	public String getSido() {
		return sido;
	}

	public void setSido(String sido) {
		this.sido = sido;
	}

	public String getGugun() {
		return gugun;
	}

	public void setGugun(String gugun) {
		this.gugun = gugun;
	}

	public String getFirstImage1() {
		return firstImage1;
	}

	public void setFirstImage1(String firstImage1) {
		this.firstImage1 = firstImage1;
	}

	public String getFirstImage2() {
		return firstImage2;
	}

	public void setFirstImage2(String firstImage2) {
		this.firstImage2 = firstImage2;
	}

	public int getMapLevel() {
		return mapLevel;
	}

	public void setMapLevel(int mapLevel) {
		this.mapLevel = mapLevel;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	@Override
	public String toString() {
		return "Attraction [no=" + no + ", contentId=" + contentId + ", title=" + title + ", contentTypeName="
				+ contentTypeName + ", sido=" + sido + ", gugun=" + gugun + ", firstImage1=" + firstImage1
				+ ", firstImage2=" + firstImage2 + ", mapLevel=" + mapLevel + ", latitude=" + latitude + ", longitude="
				+ longitude + ", tel=" + tel + ", addr=" + addr + ", homepage=" + homepage + ", overview=" + overview
				+ "]";
	}
	
	
	
}
