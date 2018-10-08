package ats.blockchain.web.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

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
	
	@Autowired
	private CordaApi cordaApi;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private List<NodeInfo> nodeInfoList;
	
	private static List<UserInfo> userList;
	
	private static Map<String,UserInfo> userInfoMap;
	
	private static Map<String,Product> productMap;
	
	private static Map<String,String> supplierMap = new HashMap<String, String>();
	
	private static Map<String,String> giaLMap = new HashMap<String, String>();
	
	private static Map<String,String> vaultMap = new HashMap<String, String>();
	
	private static Map<String,String> allUserMap = new HashMap<String, String>();
	
	public static Map<String, String> getSupplierMap()
	{
		return supplierMap;
	}

	public static Map<String, String> getGiaLMap()
	{
		return giaLMap;
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

		nodeInfoList = cordaApi.getTradediamondinf().getNodeInfos();
		this.getNodeInfoList();
	}
	
	public List<NodeInfo> getNodeInfoList()
	{
		
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
					setUserIdMap(user,giaLMap);
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
}
