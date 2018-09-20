package com.greenbirdtech.blockchain.cordapp.webdiamond;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.greenbirdtech.blockchain.cordapp.diamond.data.DiamondState;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.DiamondTradeApi;
import com.greenbirdtech.blockchain.cordapp.webdiamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.SessionListener;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.DiamondStateData;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.FormDiamondViewData;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;

@Controller
public class QueryDiamondController 
{
	private final static int QUERYDIAMONDSTATE=1;
	
	private final static Logger logger = LogManager.getLogger(QueryDiamondController.class);
	
	@Autowired
	private MenuInf menuinf;
	
	@RequestMapping(value = "/getdiamond", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView finduser(final HttpServletRequest request,final HttpServletResponse response) throws Exception
	{
		List<MenuItem> menuitemlist=null;		
		response.setCharacterEncoding(StringUtil.UTF8STR);
		if (menuinf.checkUserPermission(request))
		{
			int reqcode=0;
			int recordcount=MenuItem.NORECORDCODE;
			String retStr=null;
			ModelAndView mav = new ModelAndView("querydiamond");
			UserSession usersession = (UserSession)request.getSession(false).getAttribute(SessionListener.LOGONSESSION);
			menuitemlist = usersession.getMenuitemlist();
			DiamondTradeApi diamondtradeinf = menuinf.getTradediamondinf();
			if (diamondtradeinf != null)
			{
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
				if (reqcode == QUERYDIAMONDSTATE)
				{
					retStr = MenuItem.INVALIDPARAM;
					reqstr = request.getParameter(MenuItem.REQPARAM);
					if (!StringUtil.isNull(reqstr))
					{
						String[] strRet = reqstr.split(StringUtil.PIPESTR);
						if ((strRet.length == 2) && !StringUtil.isNull(strRet[0]) && !StringUtil.isNull(strRet[1]))
						{
							String[] strUUID = strRet[1].split(StringUtil.FIELDNAMESEPSTR);
							try
							{
								if ((strUUID.length == 2) && !StringUtil.isNull(strUUID[0]) && !StringUtil.isNull(strUUID[1]))
								{
									mav = new ModelAndView("jsonView");
									UniqueIdentifier linearid = new UniqueIdentifier(strRet[0],new UUID(Long.parseLong(strUUID[0]),Long.parseLong(strUUID[1])));
									List<StateAndRef<DiamondState>> statelist = diamondtradeinf.getAllDiamondState(linearid);
									if ((statelist != null) && (statelist.size() > 0))
									{
										List<DiamondStateData> dsdlist = new ArrayList<DiamondStateData>();
										FormDiamondViewData.formDiamondStateView(statelist, dsdlist);
										mav.addObject("STATELIST", dsdlist);
										recordcount = dsdlist.size();
									}
									else
										mav.addObject(MenuItem.REQRESULT,"Cannot find consume diamond state");
									return(mav);
								}
							}
							catch (NumberFormatException nfe)
							{
							}
						}
					}
				}
				recordcount = 0;
				List<StateAndRef<DiamondState>> diamondlist = diamondtradeinf.getAllDiamond();
				if ((diamondlist != null) && (diamondlist.size() > 0))
				{
					List<DiamondStateData> dsdlist = new ArrayList<DiamondStateData>();
					FormDiamondViewData.formDiamondView(diamondlist, dsdlist, menuinf);
					mav.addObject("DIAMONDLIST",dsdlist);
					recordcount = dsdlist.size();
				}
				else
					logger.info("No diamond is found");
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
