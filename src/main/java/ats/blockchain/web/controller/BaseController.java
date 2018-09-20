package ats.blockchain.web.controller;

import java.util.List;
import java.util.Map;

import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.model.UserInfo;

public class BaseController
{
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
