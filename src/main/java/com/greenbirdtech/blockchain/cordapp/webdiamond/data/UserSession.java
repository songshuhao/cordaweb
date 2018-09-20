package com.greenbirdtech.blockchain.cordapp.webdiamond.data;

import java.io.Serializable;
import java.util.List;

public class UserSession implements Serializable 
{
	private static final long serialVersionUID = -5497733976198018362L;

	private String userid;
	private String httpsessionid;
	private List<MenuItem> menuitemlist;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getHttpsessionid() {
		return httpsessionid;
	}
	public void setHttpsessionid(String httpsessionid) {
		this.httpsessionid = httpsessionid;
	}
	public List<MenuItem> getMenuitemlist() {
		return menuitemlist;
	}
	public void setMenuitemlist(List<MenuItem> menuitemlist) {
		this.menuitemlist = menuitemlist;
	}
}
