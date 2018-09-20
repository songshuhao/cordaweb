package ats.blockchain.web.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import ats.blockchain.web.model.Product;
import ats.blockchain.web.model.UserInfo;
import ats.blockchain.web.utils.AOCBeanUtils;

/**
 * springboot start up need load somedata
 * @author shuhao.song
 *
 */
@Component
public class DiamondApplicationRunner implements ApplicationRunner
{

	private static List<UserInfo> userList;
	
	private static Map<String,UserInfo> userInfoMap;
	
	private static Map<String,Product> productMap;
	
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

	@Override
	public void run(ApplicationArguments args) throws Exception
	{
		userList = AOCBeanUtils.getObjectFromCsv(ResourceUtils.getFile("classpath:templates/userinfo.csv").getPath(), UserInfo.class);
		if(userList!=null) {
			userInfoMap = userList.stream().collect(Collectors.toMap(UserInfo::getUserId,userInfo->userInfo,(key1,key2)->key2));
		}
		productList = AOCBeanUtils.getObjectFromCsv(ResourceUtils.getFile("classpath:templates/productspecs.csv").getPath(), Product.class);
		if(productList!=null) {
			productMap = productList.stream().collect(Collectors.toMap(Product::getProductcode,product->product,(key1,key2)->key2));
		}
		
	}
}
