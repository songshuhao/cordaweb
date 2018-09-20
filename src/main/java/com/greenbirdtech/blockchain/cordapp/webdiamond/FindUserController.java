package com.greenbirdtech.blockchain.cordapp.webdiamond;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.DiamondTradeApi;
import com.greenbirdtech.blockchain.cordapp.diamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.SessionListener;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.DisplayStrStr;

@Controller
public class FindUserController 
{
	private final static Logger logger = LogManager.getLogger(FindUserController.class);
	
	@Autowired
	private MenuInf menuinf;
	
	@RequestMapping(value = "/finduser", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView finduser(final HttpServletRequest request,final HttpServletResponse response) throws Exception
	{
		List<MenuItem> menuitemlist=null;		
		response.setCharacterEncoding(StringUtil.UTF8STR);
		if (menuinf.checkUserPermission(request))
		{
			String retStr=null;
			ModelAndView mav = new ModelAndView("finduser");
			UserSession usersession = (UserSession)request.getSession(false).getAttribute(SessionListener.LOGONSESSION);
			menuitemlist = usersession.getMenuitemlist();
			int recordcount = MenuItem.NORECORDCODE;
			DiamondTradeApi diamondtradeinf = menuinf.getTradediamondinf();
			if (diamondtradeinf != null)
			{
				try
				{
					Map<String,List<String>> mapUserinfo = diamondtradeinf.getOtherUser();
					if ((mapUserinfo != null) && (mapUserinfo.size() > 0))
					{
						List<DisplayStrStr> datalist = new ArrayList<DisplayStrStr>();
						for (Map.Entry<String,List<String>> kvp : mapUserinfo.entrySet())
						{
							for (String username : kvp.getValue())
							{
								DisplayStrStr dss = new DisplayStrStr();
								dss.setStr1(kvp.getKey());
								dss.setStr2(username);
								datalist.add(dss);
							}
						}
						recordcount = datalist.size();
						mav.addObject("USERLIST",datalist);
					}
				}
				catch (DiamondWebException de)
				{
					logger.info("Failure to get user:"+de.toString());
					retStr = de.getMsg();
				}
			}
			else
				retStr = MenuItem.RPCERROR;
			mav.addObject(MenuItem.CURRUSER,menuinf.getUsername());
			mav.addObject(MenuItem.RECORDCOUNT,Integer.valueOf(recordcount));
			if (!StringUtil.isNull(retStr))
				mav.addObject(MenuItem.REQRESULT,retStr);
			mav.addObject(MenuItem.MENUCODE,menuitemlist);
			return(mav);
		}
		request.getRequestDispatcher("/logon.jsp").forward(request,response);
		return(null);
	}
}
