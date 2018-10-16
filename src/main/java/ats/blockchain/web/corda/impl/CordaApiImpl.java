package ats.blockchain.web.corda.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.cache.CacheFactory;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.PermConfigHelperInf;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.StringUtil;
import net.corda.core.contracts.StateAndRef;
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
//	private List<NodeInfo> nodeInfos = null;
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
		initCache();
		nodeuser = diamondtradeinf.getCurrUser();
		logger.info("NodeRCPConnection is established:"+nodeuser);
	}
	
	private void initCache() {
		logger.debug("init basketno and giano cache");
		List<StateAndRef<PackageState>> list = diamondtradeinf.getAllPackageState();
		Set<String> gianoSet = Sets.newConcurrentHashSet();
		Set<String> pkgSet = Sets.newConcurrentHashSet();
		List<PackageAndDiamond> padList =Lists.newArrayList();
		list.forEach(s ->{
			PackageState pkg = s.getState().getData();
			pkgSet.add(pkg.getBasketno());
			List<DiamondsInfo> dl = pkg.getDiamondinfolist();
			if(dl!=null) {
				dl.forEach(d-> gianoSet.add(d.getGiano()));
			}else {
				padList.add(AOCBeanUtils.convertSinglePkgState2PkgInfo(s));
			}
		});
		logger.info("init basketno and giano cache end, basketno number: {},giano number: {}",pkgSet.size(),gianoSet.size());
		CacheFactory.Instance.init(gianoSet, pkgSet,padList);
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
