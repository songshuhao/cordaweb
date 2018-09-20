package com.greenbirdtech.blockchain.cordapp.webdiamond;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.greenbirdtech.blockchain.cordapp.diamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.MenuImpl;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.SessionListener;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;

@Controller
public class LogonController
{
	private final static Logger logger = LogManager.getLogger(LogonController.class);
	
	@Autowired
	private MenuInf menuinf;
	
	@RequestMapping(value = "/alogon", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView logon(final HttpServletRequest request,final HttpServletResponse response) throws Exception
	{
		response.setCharacterEncoding(StringUtil.UTF8STR);
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");
		String retMsg = "Invalid logon credential";
		if (!StringUtil.isNull(userid) && !StringUtil.isNull(password))
		{
			if (menuinf.verifyLogon(userid, password))
			{
				HttpSession session = request.getSession(false);
				if (session != null)
					session.invalidate();
				session = request.getSession(true);
				UserSession usersession = menuinf.createUsersession();
				usersession.setMenuitemlist(menuinf.getDefmenuitem());
				usersession.setHttpsessionid(session.getId());
				session.setAttribute(SessionListener.LOGONSESSION,usersession);
				session.setMaxInactiveInterval(SessionListener.SESSIONLENGTH);
				logger.info(String.format("User logon with session %1$s:%2$s",session.getId(),userid));
				ModelAndView mav = new ModelAndView("main");
				mav.addObject(MenuItem.MENUCODE,menuinf.getDefmenuitem());
				mav.addObject(MenuItem.CURRUSER,menuinf.getUsername());
				mav.addObject("GREETMSG","Welcome to Diamond Trading System");
				return(mav);
			}
		}
		request.setAttribute(MenuImpl.RETMSG,retMsg);
		request.getRequestDispatcher("/logon.jsp").forward(request,response);
		return(null);
	}
}
