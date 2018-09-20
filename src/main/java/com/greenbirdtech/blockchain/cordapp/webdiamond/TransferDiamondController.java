package com.greenbirdtech.blockchain.cordapp.webdiamond;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
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

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;

@Controller
public class TransferDiamondController 
{
	private final static int TRANSFERDIAMOND=1;
	private final static int TRANSFERRESPDIAMOND=2;
	
	private final static Logger logger = LogManager.getLogger(TransferDiamondController.class);
	
	@Autowired
	private MenuInf menuinf;
	
	@RequestMapping(value = "/transferdiamond", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView transferdiamond(final HttpServletRequest request,final HttpServletResponse response) throws Exception
	{
		List<MenuItem> menuitemlist=null;		
		response.setCharacterEncoding(StringUtil.UTF8STR);
		if (menuinf.checkUserPermission(request))
		{
			int reqcode=0;
			int recordcount=MenuItem.NORECORDCODE;
			String retStr=null;
			ModelAndView mav = new ModelAndView("transferdiamond");
			UserSession usersession = (UserSession)request.getSession(false).getAttribute(SessionListener.LOGONSESSION);
			List<String> userlist = new ArrayList<String>();
			menuitemlist = usersession.getMenuitemlist();
			DiamondTradeApi diamondtradeinf = menuinf.getTradediamondinf();
			if (diamondtradeinf != null)
			{
				String[] strRet=null;
				String reqstr = request.getParameter(MenuItem.REQCODE);
				recordcount = 0;
				if (!StringUtil.isNull(reqstr))
				{
					try
					{
						reqcode = Integer.parseInt(reqstr);
					}
					catch (NumberFormatException nfe)
					{
						logger.warn("Invalid request code format");
					}
				}
				reqstr = request.getParameter(MenuItem.REQPARAM);
				if (reqcode == TRANSFERDIAMOND)
				{
					retStr = MenuItem.INVALIDPARAM;
					if (!StringUtil.isNull(reqstr))
					{
						strRet = reqstr.split(StringUtil.PIPESTR);
						if ((strRet.length == 6) && !StringUtil.isNull(strRet[0]) && !StringUtil.isNull(strRet[1]) && !StringUtil.isNull(strRet[2]) &&
							!StringUtil.isNull(strRet[3]) && !StringUtil.isNull(strRet[4]) && !StringUtil.isNull(strRet[5]))
						{
							try
							{
								String[] strUUID = strRet[1].split(StringUtil.FIELDNAMESEPSTR);
								if ((strUUID.length == 2) && !StringUtil.isNull(strUUID[0]) && !StringUtil.isNull(strUUID[1]))
								{
									UniqueIdentifier linearid = new UniqueIdentifier(strRet[0],new UUID(Long.parseLong(strUUID[0]),Long.parseLong(strUUID[1])));
									Amount<Currency> amount = new Amount<Currency>(Long.parseLong(strRet[3]),Currency.getInstance(strRet[4]));
									logger.info(String.format("Transferring diamond %1$s to owner %2$s",strRet[0],strRet[2]));
									retStr = diamondtradeinf.transferDiamond(strRet[2],linearid,amount,strRet[5]);
								}
							}
							catch (NumberFormatException nfe)
							{
								retStr = "Invalid diamond trade id";
							}
							catch (DiamondWebException de)
							{
								logger.warn("Failure to transfer diamond:"+de.toString());
								retStr = de.getMsg();
							}
						}
					}
				}
				else if (reqcode == TRANSFERRESPDIAMOND)
				{
					retStr = MenuItem.INVALIDPARAM;
					if (!StringUtil.isNull(reqstr))
					{
						strRet = reqstr.split(StringUtil.PIPESTR);
						if ((strRet.length == 3) && !StringUtil.isNull(strRet[0]) && !StringUtil.isNull(strRet[1]) && !StringUtil.isNull(strRet[2]))
						{
							try
							{
								String[] strUUID = strRet[1].split(StringUtil.FIELDNAMESEPSTR);
								if ((strUUID.length == 2) && !StringUtil.isNull(strUUID[0]) && !StringUtil.isNull(strUUID[1]))
								{
									UniqueIdentifier linearid = new UniqueIdentifier(strRet[0],new UUID(Long.parseLong(strUUID[0]),Long.parseLong(strUUID[1])));
									logger.info(String.format("Diamond $1$s is confirmed by %2$s",strRet[0],strRet[2]));
									retStr = diamondtradeinf.transferrespDiamond(linearid,strRet[2]);
								}
							}
							catch (NumberFormatException nfe)
							{
								retStr = "Invalid diamond trade id";
							}
							catch (DiamondWebException de)
							{
								logger.warn("Failure to process diamond transfer response:"+de.toString());
								retStr = de.getMsg();
							}
						}
					}
				}
				List<StateAndRef<DiamondState>> diamondlist = diamondtradeinf.getAllDiamond();
				if ((diamondlist != null) && (diamondlist.size() > 0))
				{
					List<DiamondStateData> dsdlist = new ArrayList<DiamondStateData>();
					FormDiamondViewData.formDiamondView(diamondlist, dsdlist, menuinf);
					mav.addObject("DIAMONDLIST",dsdlist);
					recordcount = dsdlist.size();
				}
			}
			else
				retStr = MenuItem.RPCERROR;
			try
			{
				Map<String,List<String>> mapUserinfo = diamondtradeinf.getOtherUser();
				if ((mapUserinfo != null) && (mapUserinfo.size() > 0))
				{
					for (Map.Entry<String,List<String>> kvp : mapUserinfo.entrySet())
					{
						for (String username : kvp.getValue())
							userlist.add(username);
					}
				}
			}
			catch (DiamondWebException dwe)
			{
				logger.warn("Failure to get other user:"+dwe.getMsg());
			}
			mav.addObject(MenuItem.CURRUSER,menuinf.getUsername());
			mav.addObject("USERLIST",userlist);
			mav.addObject(MenuItem.RECORDCOUNT,Integer.valueOf(recordcount));
			if (!StringUtil.isNull(retStr))
				mav.addObject(MenuItem.REQRESULT,retStr);
			mav.addObject("CURRLIST",MenuItem.CURRENCYLIST);
			mav.addObject(MenuItem.MENUCODE,menuitemlist);
			return(mav);
		}
		request.getRequestDispatcher("/logon.jsp").forward(request,response);
		return(null);
	}
}
