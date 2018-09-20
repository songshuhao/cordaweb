package com.greenbirdtech.blockchain.cordapp.webdiamond.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.greenbirdtech.blockchain.cordapp.diamond.util.StringUtil;
import com.greenbirdtech.blockchain.cordapp.webdiamond.DiamondWebException;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.MenuItem;
import com.greenbirdtech.blockchain.cordapp.webdiamond.data.UserSession;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.PermConfigHelperInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.view.DiamondInfoData;

import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;

public class MenuImpl implements MenuInf 
{
	public final static String ITEMSELECT="ITEMSELECT";
	public final static String RESULTMSG="RESULTMSG";
	public final static String RETMSG="RETMSG";
	public final static String UNKNOWNMSG="--";
	public final static String HOMEURL="/index.html";
	public final static String UNAUTHACCESS="Unauthorized access";
	
	public final static String[] MAINMENU = new String[] {
			"Online Enquiry","Diamond Activity","Diamond Management"
		};
		
	public final static String[] SUBMENU = new String[] {
		"User","Diamond",
		"Issue Diamond","Transfer Diamond","Redeem Diamond",
		"Audit Diamond","Move Diamond Location"
	};
		
	public final static int[] SUBMENUPARENT = new int[] {
		0,0,
		1,1,1,
		2,2
	};
		
	public final static String[] SUBMENUURL = new String[] {
		"/finduser","/getdiamond",
		"/issuediamond","/transferdiamond","/redeemdiamond",
		"/auditdiamond","/movediamondloc"
	};
		
	public final static String[] SUBMENUFULLURL = new String[] {
		"/finduser","/getdiamond",
		"/issuediamond","/transferdiamond","/redeemdiamond",
		"/auditdiamond","/movediamondloc"
	};

	private final static int MAXSEQ=1000;
	private final static String LOCALHOSTADDR="127.0.0.1";
	
	private final static Logger logger = LogManager.getLogger(MenuImpl.class);
	
	@Autowired
	private PermConfigHelperInf permconfighelperinf;
	
	private int[] mainmenucount = new int[MAINMENU.length];
	private Map<String,Integer> mapMenu = new HashMap<String,Integer>();
	private NodeRPCConnection noderpcconn=null;
	private DiamondTradeApi diamondtradeinf;
	private int seq=0;
	private List<MenuItem> defmenuitemlist;
	private String nodeuser=null;
	private String username=null;
	private String password=null;
	private Map<String,List<DiamondInfoData>> mapDiamondinfo = new HashMap<String,List<DiamondInfoData>>();
	
	public MenuImpl()
	{
		int i;
		for (i=0; i<mainmenucount.length; i++)
			mainmenucount[i] = 0;
		for (i=0; i<SUBMENUPARENT.length; i++)
			++mainmenucount[SUBMENUPARENT[i]];
		for (i=0; i<SUBMENUURL.length;i++)
			mapMenu.put(SUBMENUFULLURL[i],i+1);
	}
	
	public void init() throws DiamondWebException
	{
		String serverip=null;
		int port = 0;
		
		if (permconfighelperinf.getRole() == 0)
			throw new DiamondWebException("Cannot determine role");
		if ((permconfighelperinf.getRpcsettingstruct() != null) && !StringUtil.isNull(permconfighelperinf.getRpcsettingstruct().getAddress()))
		{
			String[] strTemp = permconfighelperinf.getRpcsettingstruct().getAddress().split(StringUtil.COLONSTR);
			if ((strTemp.length == 2) && !StringUtil.isNull(strTemp[0]) && !StringUtil.isNull(strTemp[1]))
			{
				serverip = strTemp[0].equals(StringUtil.LOCALHOSTSTR) ? LOCALHOSTADDR : strTemp[0];
				try
				{
					port = Integer.parseInt(strTemp[1]);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
		}
		if (permconfighelperinf.getRpcuserstruct() != null)
		{
			if (!StringUtil.isNull(permconfighelperinf.getRpcuserstruct().getUser()))
				username = permconfighelperinf.getRpcuserstruct().getUser();
			if (!StringUtil.isNull(permconfighelperinf.getRpcuserstruct().getPassword()))
				password = permconfighelperinf.getRpcuserstruct().getPassword();
		}
		if (StringUtil.isNull(serverip) || (port == 0) || StringUtil.isNull(username) || StringUtil.isNull(password))
			throw new DiamondWebException("RPC information is not found");
		noderpcconn = new NodeRPCConnection(serverip,port,username,password);
		String perm = permconfighelperinf.getPerm();
		if (StringUtil.isNull(perm))
			throw new DiamondWebException("Cannot determine role permission");
		defmenuitemlist = buildMenu(perm);
		noderpcconn.startConnection();
		logger.info("Start to interface with node");
		NodeInfo nInfo = noderpcconn.getCordarpcops().nodeInfo();
		if(nInfo != null) {
			logger.info("********************");
			List<Party> pList = nInfo.getLegalIdentities();
			if(pList != null && pList.size() > 0) {
				logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				Party p = pList.get(0);
				logger.info(p.toString());
			}
		}
		diamondtradeinf = new DiamondTradeApi(noderpcconn.getCordarpcops());
		nodeuser = diamondtradeinf.getCurrUser();
		logger.info("NodeRCPConnection is established:"+nodeuser);
	}
	
	public void cleanup()
	{
		if (noderpcconn != null)
		{
			noderpcconn.closeConnection();
			noderpcconn = null;
		}
	}
	
	@Override
	public HttpSession createSession(HttpServletRequest request) 
	{
		HttpSession session = request.getSession(false);
		if (session == null)
		{
			session = request.getSession(true);
			UserSession usersession = createUsersession();
			usersession.setMenuitemlist(defmenuitemlist);
			usersession.setHttpsessionid(session.getId());
			session.setAttribute(SessionListener.LOGONSESSION,usersession);
			session.setMaxInactiveInterval(SessionListener.SESSIONLENGTH);
		}
		return(session);
	}

	@Override
	public String getUsername() 
	{
		return(nodeuser);
	}

	@Override
	public boolean verifyLogon(String user,String password)
	{
		boolean bRet=false;
		if (!StringUtil.isNull(this.username) && !StringUtil.isNull(this.password))
		{
			bRet = this.username.equals(user) && this.password.equals(password);
		}
		return(bRet);
	}
	
	@Override
	public List<MenuItem> getDefmenuitem() 
	{
		return(defmenuitemlist);
	}

	@Override
	public List<MenuItem> buildMenu(String rolemap) throws DiamondWebException 
	{
		if (rolemap.length() < SUBMENU.length)
		{
			logger.warn("Invalid role map setting");
			throw new DiamondWebException("Role map configuration error");
		}
		List<MenuItem> menuitemlist = new ArrayList<MenuItem>();
		int lastMain=-1;
		MenuItem menuitem=null;
		for (int i=0; i<SUBMENU.length; i++)
		{
			if (rolemap.charAt(i) == '1')
			{
				if ((lastMain == -1) || (SUBMENUPARENT[i] != lastMain))
				{
					lastMain = SUBMENUPARENT[i];
					if (mainmenucount[lastMain] > 1)
					{
						menuitem = new MenuItem(MAINMENU[lastMain],null,null,false,MenuItem.INVALIDREQCODE);
						menuitem.addMenuItem(new MenuItem(SUBMENU[i],SUBMENUURL[i],null,false,i+1));
					}
					else
						menuitem = new MenuItem(SUBMENU[i],SUBMENUURL[i],null,false,i+1);
					menuitemlist.add(menuitem);
				}
				else
					menuitem.addMenuItem(new MenuItem(SUBMENU[i],SUBMENUURL[i],null,false,i+1));
			}
		}
		return(menuitemlist);
	}

	@Override
	public boolean checkUserPermission(ServletRequest request) 
	{
		boolean bRet=false;
		HttpServletRequest req=null;
		if (request instanceof HttpServletRequest)
		{
			req = (HttpServletRequest)request;
			String incomingurl = req.getRequestURI();
			HttpSession session = req.getSession(false);
			if (session != null)
			{
				UserSession usersession = (UserSession)session.getAttribute(SessionListener.LOGONSESSION);
				if (usersession != null)
					bRet = checkValidReq(usersession.getMenuitemlist(),incomingurl);
				else
					logger.warn("User session is not identified");
			}
			else
				logger.warn("Session not found");
		}
		else
			logger.warn("Invalid servlet object");		
		return(bRet);
	}

	@Override
	public boolean checkValidReq(List<MenuItem> menuitemlist, String requrl) 
	{
		boolean bRet=false;
		Integer intCode = mapMenu.get(requrl);
		if (intCode != null)
		{
			int reqcode = intCode.intValue();
			for (MenuItem menuitem : menuitemlist)
			{
				menuitem.setSelected(false);
				if (menuitem.getReqcode() == MenuItem.INVALIDREQCODE)
				{
					List<MenuItem> submenulist = menuitem.getMenuItems();
					if ((submenulist != null) && (submenulist.size() > 0))
					{
						for (MenuItem subitem : submenulist)
						{
							if (subitem.getReqcode() == reqcode)
							{
								subitem.setSelected(true);
								bRet = true;
							}
						}
					}
					else
						logger.warn("Menu item configuration mismatch");
				}
				else
				{
					if (menuitem.getReqcode() == reqcode)
					{
						menuitem.setSelected(true);
						bRet = true;
					}
				}
			}
		}
		return(bRet);
	}

	@Override
	public DiamondTradeApi getTradediamondinf() 
	{
		return(diamondtradeinf);
	}

	@Override
	public void updateDiamondinfo(String externalid,List<DiamondInfoData> didlist)
	{
		mapDiamondinfo.put(externalid,didlist);
	}
	
	@Override
	public List<DiamondInfoData> getDiamondInfo(String externalid)
	{
		return(mapDiamondinfo.get(externalid));
	}
	
	@Override
	public synchronized UserSession createUsersession()
	{
		UserSession usersession = new UserSession();
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(username);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(seq);
		strbuf.append(StringUtil.FIELDNAMESEP);
		strbuf.append(Long.toHexString(System.currentTimeMillis()));
		usersession.setUserid(strbuf.toString());
		if (++seq >= MAXSEQ)
			seq = 0;
		return(usersession);
	}
}
