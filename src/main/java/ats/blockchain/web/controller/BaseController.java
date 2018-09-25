package ats.blockchain.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.model.UserInfo;
import ats.blockchain.web.utils.Constants;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;

public class BaseController
{
	
	@Autowired
	private CordaApi cordaApi;
	private List<UserInfo> userList;
	private UserInfo currentUserInfo;
	
	private List<Product> productList;
	
	private Product product;
	
	private Map<String,Product> productMap;
	
	private List<NodeInfo> nodeInfoList;
	
	private Map<String,String> supplierMap;
	
	private Map<String,String> giaLMap;
	
	private Map<String,String> vaultMap;
	
	public List<NodeInfo> getNodeInfoList()
	{
		nodeInfoList = cordaApi.getTradediamondinf().getNodeInfos();
		supplierMap = new HashMap<String, String>();
		giaLMap = new HashMap<String, String>();
		vaultMap = new HashMap<String, String>();
		
		for(NodeInfo nodeInfo : nodeInfoList)
		{
			Party p = nodeInfo.getLegalIdentities().get(0);
			if(null != p)
			{
				String user = p.toString();
				if(user.contains(Constants.ROLE_SUPPLIER))
				{
					this.setUserIdMap(user,supplierMap);
				}else if(user.contains(Constants.ROLE_LAB))
				{
					this.setUserIdMap(user,giaLMap);
				}else if(user.contains(Constants.ROLE_VAULT))
				{
					this.setUserIdMap(user,vaultMap);
				}
			}
		}
		this.setSupplierMap(supplierMap);
		this.setGiaLMap(giaLMap);
		this.setVaultMap(vaultMap);
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
	
	public void setNodeInfoList(List<NodeInfo> nodeInfoList)
	{
		this.nodeInfoList = nodeInfoList;
	}
	
	public Map<String, String> getSupplierMap()
	{
		return supplierMap;
	}

	public void setSupplierMap(Map<String, String> supplierMap)
	{
		this.supplierMap = supplierMap;
	}

	public Map<String, String> getGiaLMap()
	{
		return giaLMap;
	}

	public void setGiaLMap(Map<String, String> giaLMap)
	{
		this.giaLMap = giaLMap;
	}

	public Map<String, String> getVaultMap()
	{
		return vaultMap;
	}

	public void setVaultMap(Map<String, String> vaultMap)
	{
		this.vaultMap = vaultMap;
	}

	public Map<String, Product> getProductMap()
	{
		productMap = DiamondApplicationRunner.getProductMap();
		return productMap;
	}
	public void setProductMap(Map<String, Product> productMap)
	{
		this.productMap = productMap;
	}
	
	public Product getProduct()
	{
		return product;
	}
	public void setProduct(Product product)
	{
		this.product = product;
	}
	public List<Product> getProductList()
	{
		productList = DiamondApplicationRunner.getProductList();
		return productList;
	}
	public void setProductList(List<Product> productList)
	{
		this.productList = productList;
	}
	public UserInfo getCurrentUserInfo()
	{
		String user = cordaApi.getTradediamondinf().getCurrUser();
		String role = "";
		if(!StringUtils.isBlank(user))
		{
			String[] userArray = user.split(",");
			for(String info : userArray)
			{
				if(info.contains("OU="))
				{
					role = info.split("[=]")[1];
					break;
				}
			}
			currentUserInfo.setRole(role);
			currentUserInfo.setMyLegalName(user);
		}
		return currentUserInfo;
	}
	public void setCurrentUserInfo(UserInfo userInfo)
	{
		this.currentUserInfo = userInfo;
	}
	public List<UserInfo> getUserList()
	{
		userList = DiamondApplicationRunner.getUserList();
		return userList;
	}
	public void setUserList(List<UserInfo> userList)
	{
		this.userList = userList;
	}
	
	public UserInfo getUserInfo(String userId) {
		return DiamondApplicationRunner.getUserInfoMap().get(userId);
	}
	
	public String getUserLegalName(String userId) {
		UserInfo user = DiamondApplicationRunner.getUserInfoMap().get(userId);
		if(user==null) {
			throw new IllegalArgumentException("invaild userId:"+userId);
		}
		return user.getMyLegalName();
		
	}
}
