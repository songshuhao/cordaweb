package com.greenbirdtech.blockchain.cordapp.webdiamond.inf;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.DiamondTradeApi;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.DiamondInfoData;
import com.greenbirdtech.blockchain.cordapp.webdiamond.DiamondWebException;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;

public interface MenuInf 
{
	public HttpSession createSession(HttpServletRequest request);
	public String getUsername();
	public boolean verifyLogon(String user,String password);
	public List<MenuItem> getDefmenuitem();
	public List<MenuItem> buildMenu(String rolemap) throws DiamondWebException;
	public boolean checkUserPermission(ServletRequest request);
	public boolean checkValidReq(List<MenuItem> menuitemlist,String requrl);
	public DiamondTradeApi getTradediamondinf();
	public void updateDiamondinfo(String externalid,List<DiamondInfoData> didlist);
	public List<DiamondInfoData> getDiamondInfo(String externalid);
	public UserSession createUsersession();
}
