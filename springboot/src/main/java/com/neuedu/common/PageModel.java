package com.neuedu.common;

import java.util.List;

public class PageModel<T> {



	private int pagNum;
	private int pageSize;
	private int size;
	private String orderBy;
	private int startRow;
	private int endRow;
	private int total;
	private int pages;
	private List<T> list;

	public int getPagNum() {
		return pagNum;
	}

	public void setPagNum(int pagNum) {
		this.pagNum = pagNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "PageModel [pagNum=" + pagNum + ",size="+size+", pageSize=" + pageSize + ",orderBy="+orderBy+", startRow=" + startRow
				+ ", endRow=" + endRow + ", total=" + total + ",pages="+pages+",list="+list+"]";
	}
	
	
	
	
}
