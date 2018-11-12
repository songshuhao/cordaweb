package ats.blockchain.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import ats.blockchain.cordapp.diamond.util.StateInfo;
import ats.blockchain.cordapp.diamond.util.StringUtil;
import ats.blockchain.web.bean.ExportConfig;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.model.UserInfo;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;

/**
 * springboot start up need load somedata
 * @author shuhao.song
 *
 */
@Component
public class DiamondApplicationRunner implements ApplicationRunner
{
	
	@Value("${user.path}")
	private String userPath;
	
	@Value("${product.path}")
	private String productPath;
	@Value("${exportConfig.path}")
	private String exportConfigPath;
	
	@Autowired
	private CordaApi cordaApi;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private List<NodeInfo> nodeInfoList;
	
	private static List<UserInfo> userList;
	
	private static Map<String,UserInfo> userInfoMap;
	
	private static Map<String,Product> productMap;
	
	private static Map<String,String> supplierMap = new HashMap<String, String>();
	
	private static Map<String,String> giaMap = new HashMap<String, String>();
	
	private static Map<String,String> vaultMap = new HashMap<String, String>();
	
	private static Map<String,String> allUserMap = new HashMap<String, String>();
	private static Map<String,ExportConfig> exportCfgMap = new HashMap<String, ExportConfig>();
	
	public static Map<String, String> getSupplierMap()
	{
		return supplierMap;
	}

	public static Map<String, String> getGiaMap()
	{
		return giaMap;
	}

	public static Map<String, String> getVaultMap()
	{
		return vaultMap;
	}

	public static Map<String, Product> getProductMap()
	{
		return productMap;
	}

	public static List<UserInfo> getUserList()
	{
		return userList;
	}
	
	private static List<Product> productList;
	
	public static List<Product> getProductList()
	{
		return productList;
	}
	
	public static Map<String,UserInfo> getUserInfoMap(){
		return userInfoMap;
	}
	
	public static Map<String, String> getAllUserMap()
	{
		return allUserMap;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		initUserInfo();
		initProduct();
		initExportConfig();
		initNodeInfoList();
	}

	private void initUserInfo() throws IOException, InstantiationException, IllegalAccessException {
		Resource userResource = resourceLoader.getResource(userPath);
		InputStream userStream = userResource.getInputStream();
		try {
			userList = AOCBeanUtils.getObjectFromCsv(userStream, UserInfo.class);
			if (userList != null) {
				userInfoMap = userList.stream()
						.collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo, (key1, key2) -> key2));
			}
		} finally {
			if (userStream != null) {
				userStream.close();
			}
		}
	}

	private void initProduct() throws IOException, InstantiationException, IllegalAccessException {
		Resource productResource = resourceLoader.getResource(productPath);
		InputStream productStream = productResource.getInputStream();
		try {
			productList = AOCBeanUtils.getObjectFromCsv(productStream, Product.class);
			if (productList != null) {
				productMap = productList.stream()
						.collect(Collectors.toMap(Product::getProductcode, product -> product, (key1, key2) -> key2));
			}
		} finally {
			if (productStream != null) {
				productStream.close();
			}
		}
	}
	
	private void initExportConfig() throws InstantiationException, IllegalAccessException, IOException {
		Resource res = resourceLoader.getResource(exportConfigPath);
		InputStream in = res.getInputStream();
		try {
			List<ExportConfig> list = AOCBeanUtils.getObjectFromCsv(in, ExportConfig.class);
			exportCfgMap = list.stream().collect(Collectors.toMap(ExportConfig::getStep, e->e,(key1,key2)->key2));
		}finally {
			if(in!=null) {
				in.close();
			}
		}
	}
	
	
	public List<NodeInfo> initNodeInfoList()
	{
		nodeInfoList = cordaApi.getTradediamondinf().getNodeInfos();
		for(NodeInfo nodeInfo : nodeInfoList)
		{
			Party p = nodeInfo.getLegalIdentities().get(0);
			if(null != p)
			{
				String user = p.toString();
				setUserIdMap(user, allUserMap);
				if(user.contains(Constants.ROLE_SUPPLIER))
				{
					setUserIdMap(user,supplierMap);
				}else if(user.contains(Constants.ROLE_LAB))
				{
					setUserIdMap(user,giaMap);
				}else if(user.contains(Constants.ROLE_VAULT))
				{
					setUserIdMap(user,vaultMap);
				}
			}
		}
		return nodeInfoList;
	}
	
	private void setUserIdMap(String user, Map<String,String> map)
	{
		String[] userArray = user.split(",");
		String userId = "";
		for(String info : userArray)
		{
			if(info.contains("O="))
			{
				userId = info.split("[=]")[1];
				map.put(userId, user);
				break;
			}
		}
	}

	public static Map<String, ExportConfig> getExportConfig() {
		return exportCfgMap;
	}
	
	public static Map<String,StateInfo> stateInfoMap = new HashMap<String,StateInfo> ();
	
	public static Set<String> finalStatus = new HashSet<String>();
	
	public void initPreState() {
		List<StateInfo> list = null;
		try {
			InputStream is=ClassLoader.getSystemResourceAsStream("status.properties");
			list = StringUtil.getObjectFromCsv(is , StateInfo.class);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IOException e) {
		}
		
		if(list!=null) {
			for(StateInfo s :list) {
				stateInfoMap.put(s.getStatus(),s);
				if(s.isFinalStatus()) {
					finalStatus.add(s.getStatus());
				}
			}
		}
	}
}
