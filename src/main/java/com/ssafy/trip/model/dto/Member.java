package com.ssafy.trip.model.dto;

public class Member {
    private int mno;
    private String id;
    private String password;
    private String name;
    private String role;
    private String address;
    private String tel;

    public Member() {
        super();
    }
    
    public Member(String name, String id, String password,String address, String tel ) {
        this(0, id, password, name, null, address , tel);
    }


    public Member(int mno, String id, String password, String name, String role, String address, String tel) {
        this.mno = mno;
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
        this.address =  address;
        this.tel =  tel;
    }



	public int getMno() {
		return mno;
	}

	public void setMno(int mno) {
		this.mno = mno;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Override
	public String toString() {
		return "Member [mno=" + mno + ", id=" + id + ", password=" + password + ", name=" + name + ", role=" + role
				+ ", address=" + address + "]";
	}



}
