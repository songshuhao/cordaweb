package com.greenbirdtech.blockchain.cordapp.webdiamond.impl;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;
import com.greenbirdtech.blockchain.cordapp.webdiamond.util.StringUtil;

public class SessionListener implements HttpSessionListener 
{
	public static final String SESSIONID="SESSIONID";
	public static final String LOGONSESSION="LOGONSESSION";
	
	public static final int SESSIONLENGTH=60*60;
	
	private final static Logger logger = LogManager.getLogger(SessionListener.class);
	
	private StringBuilder strbuf = new StringBuilder();
	private Random random = new Random(System.currentTimeMillis());
	
	@Override
	public void sessionCreated(HttpSessionEvent event) 
	{
		String sessionid = getSessionId();
		event.getSession().setAttribute(SESSIONID,sessionid);
		event.getSession().setMaxInactiveInterval(SESSIONLENGTH);
		logger.info("New session is created:"+sessionid);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) 
	{
		HttpSession httpsession = event.getSession();
		UserSession usersession = (UserSession)httpsession.getAttribute(LOGONSESSION);
		if (usersession != null)
			httpsession.removeAttribute(LOGONSESSION);
		String sessionid = (String)httpsession.getAttribute(SESSIONID);
		if (!StringUtil.isNull(sessionid))
			logger.info("Session is destroyed:"+sessionid);
	}

	private synchronized String getSessionId()
	{
		strbuf.setLength(0);
		strbuf.append(Long.toHexString(System.currentTimeMillis()));
		strbuf.append('_');
		strbuf.append(random.nextInt(128));
		return(strbuf.toString());
	}
}
