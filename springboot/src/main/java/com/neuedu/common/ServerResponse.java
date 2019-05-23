package com.neuedu.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;

/*
* 返回JSON格式时，如果为空则不显示。
* */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
	
	
	private int status;
	private String msg;
	private T date;
	
	private ServerResponse(int status) {
		super();
		this.status = status;
	}

	private ServerResponse(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	private ServerResponse(int status, T date) {
		super();
		this.status = status;
		this.date = date;
	}

	private ServerResponse(int status, String msg, T date) {
		super();
		this.status = status;
		this.msg = msg;
		this.date = date;
	}
	
	/*

	 * */
	@JsonIgnore
	public  boolean isSucess() {
		return this.status==0;
	}
	
	public static ServerResponse ServerResponsecreateBySucess(){
		
		
		return new ServerResponse(0);
	}
	
	public static ServerResponse ServerResponsecreateBySucess(String msg){
		
		
		return new ServerResponse(0,msg);
	}
	
	public static <T> ServerResponse<T> ServerResponsecreateBySucess(String msg, T date){
		
		
		return new ServerResponse<T>(0,msg,date);
	}
	public static <T> ServerResponse<T> ServerResponsecreateBySucess( T date){


		return new ServerResponse<T>(0,date);
	}
	
	
	
	
	public static ServerResponse ServerResponsecreateByFail(int status){
		
		
		return new ServerResponse(status);
	}
	public static ServerResponse ServerResponsecreateByFail(String msg){

		return new ServerResponse(1,msg);
	}
	public static ServerResponse ServerResponsecreateByFail(int status, String msg){
		
		
		return new ServerResponse(status,msg);
	}
	
	
	
	public String objtostr() {
		Gson gson=new Gson();
		
		String responseText =gson.toJson(this);
		return responseText;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getDate() {
		return date;
	}

	public void setDate(T date) {
		this.date = date;
	}


	
}
