package ats.blockchain.web.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.model.UserInfo;
import ats.blockchain.web.servcie.DiamondsInfoService;

public class BaseController
{
	@Resource(name="diamondsInfoServiceCordaImpl")
	public DiamondsInfoService diamondsInfoService;
	
	@Autowired
	public CordaApi cordaApi;
	
	private List<UserInfo> userList;
	private UserInfo currentUserInfo;
	
	private List<Product> productList;
	
	private Product product;
	
	private Map<String,Product> productMap;
	
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
		}else
		{
			throw new IllegalArgumentException("cordaApi userInfo is null:");
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
		String userLegalName = DiamondApplicationRunner.getAllUserMap().get(userId);
		if(StringUtils.isBlank(userLegalName)) {
			throw new IllegalArgumentException("invaild userId:"+userId);
		}
		return userLegalName;
		
	}
}
