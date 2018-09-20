package com.greenbirdtech.blockchain.cordapp.webdiamond.view;

import java.io.Serializable;

public class DisplayStrStr implements Serializable 
{
	private static final long serialVersionUID = -7387851912140303380L;

	private String str1;
	private String str2;
	
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
}
