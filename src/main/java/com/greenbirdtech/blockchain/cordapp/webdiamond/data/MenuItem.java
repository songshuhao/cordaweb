package com.greenbirdtech.blockchain.cordapp.webdiamond.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MenuItem implements Serializable 
{
	private static final long serialVersionUID = -295074935863571165L;

	public static final int INVALIDREQCODE=-1;
	public static final int NORECORDCODE=-1;
	public static final String CURRUSER="CURRUSER";
	public static final String REQCODE="REQCODE";
	public static final String REQPARAM="REQPARAM";
	public static final String REQRESULT="REQRESULT";
	public static final String MENUCODE="verticalMenu";
	public static final String RECORDCOUNT="RECORDCOUNT";
	public static final char REQFIELDSEP='|';
	public static final String REQFIELDSTR="\\|";
	public static final String INVALIDPARAM="Invalid Parameter";
	public static final String RPCERROR="NodeRPCConnection error";
	public static final List<String> CURRENCYLIST = Collections.unmodifiableList(Arrays.asList("USD","CNY","EUR","HKD","INR","JPY","AUD","CHF"));
	
	private String item;
	private String url;
	private int reqcode;
	private List<MenuItem> menuItems;
	private boolean selected;
	
	public MenuItem(String item,String url,List<MenuItem> menuItems,boolean selected,int reqcode)
	{
		super();
		this.item = item;
		this.url = url;
		this.menuItems = menuItems;
		this.selected = selected;
		this.reqcode = reqcode;
	}
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void addMenuItem(MenuItem menuItem)
	{
		if (menuItems == null)
			menuItems = new ArrayList<MenuItem>();
		menuItems.add(menuItem);
	}

	public int getReqcode() {
		return reqcode;
	}

	public void setReqcode(int reqcode) {
		this.reqcode = reqcode;
	}
}
