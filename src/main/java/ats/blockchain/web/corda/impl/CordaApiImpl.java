package ats.blockchain.web.corda.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.PermConfigHelperInf;
import ats.blockchain.web.utils.StringUtil;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;

public class CordaApiImpl implements CordaApi {
	private NodeRPCConnection noderpcconn=null;
	private DiamondTradeApi diamondtradeinf;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PermConfigHelperInf permconfighelperinf;
	private String nodeuser=null;
	private String username=null;
	private String password=null;
	private final static String LOCALHOSTADDR="127.0.0.1";
	@Override
	public DiamondTradeApi getTradediamondinf() {
		return diamondtradeinf;
	}
	
	public void init() throws DiamondWebException
	{
		logger.debug("CordaApiImpl init");
		String serverip=null;
		int port = 0;
		
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
		noderpcconn.startConnection();
		logger.info("Start to interface with node");
		NodeInfo nInfo = noderpcconn.getCordarpcops().nodeInfo();
		if(nInfo != null) {
			List<Party> pList = nInfo.getLegalIdentities();
			if(pList != null && pList.size() > 0) {
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
}
