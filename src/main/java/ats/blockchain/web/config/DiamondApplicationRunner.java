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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import ats.blockchain.web.bean.ExportConfig;
import ats.blockchain.web.bean.StateInfo;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.model.UserInfo;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;

/**
 * springboot start up need load somedata
 * 
 * @author shuhao.song
 *
 */
@Component
public class DiamondApplicationRunner implements ApplicationRunner {

	private static Logger logger = LoggerFactory.getLogger(DiamondApplicationRunner.class);
	@Value("${user.path}")
	private String userPath;

	@Value("${product.path}")
	private String productPath;
	@Value("${exportConfig.path}")
	private String exportConfigPath;

	@Value("${preStateCheck.path}")
	private String preStateCheck;
	public void setPreStateCheck(String preStateCheck) {
		this.preStateCheck = preStateCheck;
	}

	@Autowired
	private CordaApi cordaApi;

	
	public void setCordaApi(CordaApi cordaApi) {
		this.cordaApi = cordaApi;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Autowired
	private ResourceLoader resourceLoader;

	private List<NodeInfo> nodeInfoList;

	private static List<UserInfo> userList;

	private static Map<String, UserInfo> userInfoMap;

	private static Map<String, Product> productMap;

	private static Map<String, String> supplierMap = new HashMap<String, String>();

	private static Map<String, String> giaMap = new HashMap<String, String>();

	private static Map<String, String> vaultMap = new HashMap<String, String>();

	private static Map<String, String> allUserMap = new HashMap<String, String>();
	private static Map<String, ExportConfig> exportCfgMap = new HashMap<String, ExportConfig>();
	
	

	public static Map<String, String> getSupplierMap() {
		return supplierMap;
	}

	public static Map<String, String> getGiaMap() {
		return giaMap;
	}

	public static Map<String, String> getVaultMap() {
		return vaultMap;
	}

	public static Map<String, Product> getProductMap() {
		return productMap;
	}

	public static List<UserInfo> getUserList() {
		return userList;
	}

	private static List<Product> productList;

	public static List<Product> getProductList() {
		return productList;
	}

	public static Map<String, UserInfo> getUserInfoMap() {
		return userInfoMap;
	}

	public static Map<String, String> getAllUserMap() {
		return allUserMap;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		initUserInfo();
		initProduct();
		initExportConfig();
		initNodeInfoList();
		initPreState();
	}

	private void initUserInfo() throws IOException, InstantiationException, IllegalAccessException {
		Resource userResource = resourceLoader.getResource(userPath);
		InputStream userStream = userResource.getInputStream();
		try {
			userList = AOCBeanUtils.getObjectFromCsv(userStream, UserInfo.class);
			if (userList != null) {
				userInfoMap = userList.stream()
						.collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo, (key1, key2) -> key2));
				logger.info("init user list success. size: {}", userList.size());
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
				logger.info("init product list success. size: {}", productList.size());
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
			exportCfgMap = list.stream().collect(Collectors.toMap(ExportConfig::getStep, e -> e, (key1, key2) -> key2));
			logger.info("init export config list success. size: {}", list.size());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public List<NodeInfo> initNodeInfoList() {
		nodeInfoList = cordaApi.getTradediamondinf().getNodeInfos();
		for (NodeInfo nodeInfo : nodeInfoList) {
			Party p = nodeInfo.getLegalIdentities().get(0);
			if (null != p) {
				String user = p.toString();
				setUserIdMap(user, allUserMap);
				if (user.contains(Constants.ROLE_SUPPLIER)) {
					setUserIdMap(user, supplierMap);
				} else if (user.contains(Constants.ROLE_LAB)) {
					setUserIdMap(user, giaMap);
				} else if (user.contains(Constants.ROLE_VAULT)) {
					setUserIdMap(user, vaultMap);
				}
			}
		}
		logger.info("init node info list success. size: {}", nodeInfoList.size());
		return nodeInfoList;
	}

	private void setUserIdMap(String user, Map<String, String> map) {
		String[] userArray = user.split(",");
		String userId = "";
		for (String info : userArray) {
			if (info.contains("O=")) {
				userId = info.split("[=]")[1];
				map.put(userId, user);
				break;
			}
		}
	}

	public static Map<String, ExportConfig> getExportConfig() {
		return exportCfgMap;
	}

	public static Map<String, StateInfo> stateInfoMap = new HashMap<String, StateInfo>();

	public static Set<String> finalStatus = new HashSet<String>();

	/**
	 * 初始化前置条件检查配置 
	 */
	public void initPreState() {
		List<StateInfo> list = null;
		InputStream inputStream = null;
		try {
			Resource is = resourceLoader.getResource(preStateCheck);
			inputStream = is.getInputStream();
			list = AOCBeanUtils.getObjectFromCsvByMethod(inputStream, StateInfo.class);
			logger.info("init prestatus list success. size: {}", list.size());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | IOException | InvocationTargetException e) {
			logger.error("init prestatus error", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}

		if (list != null) {
			for (StateInfo s : list) {
				stateInfoMap.put(s.getStatus(), s);
				if (s.isFinalStatus()) {
					finalStatus.add(s.getStatus());
				}
			}
		}
	}
}
