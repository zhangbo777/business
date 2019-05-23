package com.neuedu.pojo;

public class Admin {
	
	private  Integer  id;
	private String username;
	private String password;


	public Admin(int id, String username){
		this.username=username;

		this.id=id;
	}

	public Admin(String username, String password){
		this.username=username;
		this.password=password;
	}
	public Admin(int id, String username, String password){
		this.username=username;
		this.password=password;
		this.id=id;
	}
	
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username=username;
	}
	
	public String getPassword(){
		return password;
	}
	public void setPassword(String password){
		this.password=password;
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Admin [id=" + id + ", username=" + username + ", password=" + password + "]";
	}
	
	
}
