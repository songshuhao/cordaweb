package com.greenbirdtech.blockchain.cordapp.webdiamond;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.DiamondInfoData;

@Controller
public class GetDiamondInfoController 
{
	private final static int GETDIAMOND=1;
	
	private final static Logger logger = LogManager.getLogger(GetDiamondInfoController.class);
	
	@Autowired
	private MenuInf menuinf;
	
	@RequestMapping(value = "/getdiamondinfo", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView getDiamondinfo(final HttpServletRequest request,final HttpServletResponse response)
	{
		int reqcode=0;
		
		String retStr = MenuItem.INVALIDPARAM;
		ModelAndView mav = new ModelAndView("jsonView");
		String reqstr = request.getParameter(MenuItem.REQCODE);
		if (!StringUtil.isNull(reqstr))
		{
			try
			{
				reqcode = Integer.parseInt(reqstr);
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		reqstr = request.getParameter(MenuItem.REQPARAM);
		if ((reqcode == GETDIAMOND) && !StringUtil.isNull(reqstr))
		{
			List<DiamondInfoData> didlist = menuinf.getDiamondInfo(reqstr);
			if ((didlist != null) && (didlist.size() > 0))
			{
				mav.addObject("DINFOLIST", didlist);
				retStr = null;
			}
			else
			{
				logger.warn("Cannot find diamond information from cache:"+reqstr);
				retStr = "Cannot find diamond information";
			}
		}
		if (!StringUtil.isNull(retStr))
			mav.addObject(MenuItem.REQRESULT,retStr);
		return(mav);
	}
}
