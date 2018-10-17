package ats.blockchain.web.servcie.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.cache.CacheFactory;
import ats.blockchain.web.cache.PackageCache;
import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.ResultUtil;
import net.corda.core.contracts.StateAndRef;

@Service
public class BasketInfoServcieCordaImpl implements PackageInfoService {
	@Autowired
	private CordaApi cordaApi;
	private DiamondTradeApi diamondApi;
	private Logger logger = LogManager.getLogger(getClass());

	@PostConstruct
	public void init() {
		diamondApi = cordaApi.getTradediamondinf();
	}

	@Override
	public Map<String, Object> addPackageInfo(PackageInfo pkgInf) {
		logger.debug("addBasketInfo :{}", pkgInf);

		String userid = pkgInf.getUserid();
		logger.debug("get package cache of user: {}", userid);
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		boolean flag = cache.containsPackage(pkgInf.getBasketno());
		if (flag) {
			return ResultUtil.failMap("package no duplicate :" + pkgInf.getBasketno());
		}
		pkgInf.setStatus(PackageState.PKG_CREATE);
		pkgInf.setStatusDesc(ats.blockchain.cordapp.diamond.util.Constants.PKG_STATE_MAP.get(PackageState.PKG_CREATE));
		cache.add(pkgInf);
		logger.debug("{} package add to cache.", pkgInf.getBasketno());
		return ResultUtil.msgMap(true, "success");
	}

	@Override
	public Map<String, Object> editPackageInfo(PackageInfo pkgInf) {
		logger.debug("editPackageInfo :{}", pkgInf);
		String userid = pkgInf.getUserid();
		logger.debug("get package cache of user: {}", userid);
		pkgInf.setStatus(PackageState.PKG_CREATE);
		pkgInf.setStatusDesc(ats.blockchain.cordapp.diamond.util.Constants.PKG_STATE_MAP.get(PackageState.PKG_CREATE));
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		cache.add(pkgInf);
		logger.debug("{} package update to cache.", pkgInf.getBasketno());
		return ResultUtil.msgMap(true,"edit package success");
	}
	
	@Override
	public List<PackageInfo> getPackageInfo() {
		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		logger.error("getPackageInfo unimplements!!!");

		return pkgList;
	}

	@Override
	public List<PackageInfo> submitPackageByStatus(String status, String userid) {
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		List<PackageInfo> list = cache.getPackageByStatus(status);
		List<PackageInfo> failedList = new ArrayList<>();

		for (PackageInfo pkgInf : list) {
			String basketno = pkgInf.getBasketno();
			logger.debug("submitPackage {}", basketno);
			try {
				PackageState pkgState = new PackageState();
				BeanUtils.copyProperties(pkgInf, pkgState);
				diamondApi.createPackage(pkgInf.getSuppliercode(), pkgState, PackageState.PKG_ISSUE);
				cache.remove(pkgInf.getSeqNo(), status);
			} catch (DiamondWebException e) {
				logger.error("issue package error:", e);
				failedList.add(pkgInf);
			}
		}
		return failedList;
	}

	@Override
	public List<PackageInfo> getPackageInfoByStatus(String userid, String... status) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		List<PackageInfo> pkgList = cache.getPackageByStatus(status);
	
		logger.debug("userid: {} get {} from corda size: {}", userid, Arrays.toString(status), padList.size());
		logger.debug("userid: {} get {} from cache size: {}", userid, Arrays.toString(status), pkgList.size());
		List<PackageInfo> mergeList = AOCBeanUtils.mergePackageList(pkgList, padList);
		return mergeList;
	}

	@Override
	public List<PackageState> getPackageStateByStatus(String... status) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageState> pkgList = new ArrayList<PackageState>();
		for (StateAndRef<PackageState> pad : list) {
			pkgList.add(pad.getState().getData());
		}
		return pkgList;
	}

	@Override
	public List<PackageInfo> getPackageInfoById(String... basketNo) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(basketNo);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		for (PackageAndDiamond pad : padList) {
			pkgList.add(pad.getPkgInfo());
		}
		return pkgList;
	}

	@Override
	public List<PackageState> getPackageStateById(String... basketNo) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(basketNo);
		List<PackageState> pkgList = new ArrayList<PackageState>();
		for (StateAndRef<PackageState> pad : list) {
			pkgList.add(pad.getState().getData());
		}
		return pkgList;
	}

	@Override
	public boolean labConfirmPackageInfo(PackageInfo pkgInf) {
		logger.debug("confrimPackageInfo :{}", JSON.toJSONString(pkgInf));

		boolean flag = false;
		String rs = "";
		String status = pkgInf.getStatus();
		String userid = pkgInf.getUserid();
		try {
			PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
			if (status.equals(PackageState.AOC_REQ_LAB_VERIFY)) {
				cache.add(pkgInf);
			} else if (status.equals(PackageState.LAB_ADD_VERIFY)) {
				pkgInf.setAoc(DiamondApplicationRunner.getAllUserMap().get("AOC"));
				pkgInf.setGiacontrolno("1");
				cache.add(pkgInf);
			} else {
				return flag;
			}

			logger.info("confrimPackageInfo result: {}", rs);
			flag = true;
		} catch (Exception e) {
			logger.error("confrimPackageInfo error ", e);
		}
		return flag;
	}

	@Override
	public List<PackageInfo> submitPackageInfo(String step, String userid) {
		List<PackageInfo> pkgStateList =null;
		List<PackageInfo> cachedList = Lists.newArrayList();
		List<PackageInfo> failedList = new ArrayList<>();
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		if (step.equals(Constants.AOC_TO_GIA)) {
			pkgStateList = cache.getPackageByStatus(PackageState.DMD_ISSUE);
			if (AOCBeanUtils.isNotEmpty(pkgStateList)) {
				// 校验未add的数据
			} else {
				cachedList = cache.getPackageByStatus(PackageState.AOC_REQ_LAB_VERIFY);
				for (PackageInfo pkgState : cachedList) {
					logger.debug("submitPackageInfo {}", pkgState.getBasketno());
					try {
						diamondApi.reqLabVerifyDiamond(pkgState.getBasketno(), PackageState.AOC_SUBMIT_LAB_VERIFY,
								pkgState.getGradlab().toString());
						cache.remove(pkgState.getSeqNo(), pkgState.getStatus());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:", e);
						failedList.add(pkgState);
					}
				}
			}
		} else if (step.equals(Constants.GIA_TO_AOC)) {
			pkgStateList =  cache.getPackageByStatus(PackageState.AOC_SUBMIT_LAB_VERIFY);
			if (AOCBeanUtils.isNotEmpty(pkgStateList)) {
				// 校验未add的数据
			} else {
				cachedList = cache.getPackageByStatus(PackageState.LAB_ADD_VERIFY);
				for (PackageInfo pkgState : cachedList) {
					logger.debug("submitPackageInfo {}", pkgState);
					try {
						String result = pkgState.getResult();
						if ("verified".equals(result)) {
							pkgState.setStatus(PackageState.LAB_VERIFY_PASS);
						} else if ("failure".equals(result)) {
							pkgState.setStatus(PackageState.LAB_VERIFY_NOPASS);
						}
						diamondApi.labVerifyResp(pkgState);
						cache.remove(pkgState.getSeqNo(), pkgState.getStatus());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:", e);
						failedList.add(pkgState);
					}
				}
			}
		} else if (step.equals(Constants.AOC_TO_VAULT)) {
			pkgStateList = cache.getPackageByStatus(PackageState.LAB_VERIFY_PASS);
			if (AOCBeanUtils.isNotEmpty(pkgStateList)) {
				// 校验未add的数据
			} else {
				cachedList = cache.getPackageByStatus(PackageState.AOC_REQ_VAULT_VERIFY);
				for (PackageInfo pkgState : cachedList) {
					logger.debug("submitPackageInfo {} {} ", pkgState.getBasketno(),pkgState.getStatusDesc(),pkgState.getOwner());
					try {
						diamondApi.reqVaultVerifyDiamond(pkgState.getBasketno(), PackageState.AOC_SUBMIT_VAULT_VERIFY,
								pkgState.getVault().toString(), pkgState.getOwner());
						cache.remove(pkgState.getSeqNo(), pkgState.getStatus());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:", e);
						failedList.add(pkgState);
					}
				}
			}
		} else if (step.equals(Constants.VAULT_TO_AOC)) {
			pkgStateList =  cache.getPackageByStatus(PackageState.AOC_SUBMIT_VAULT_VERIFY);
			if (AOCBeanUtils.isNotEmpty(pkgStateList)) {
				// 校验未add的数据
				for (PackageInfo state : pkgStateList) {
					logger.debug("submitPackageInfo {}", state);
					failedList.add(state);
				}
			} else {
				cachedList = cache.getPackageByStatus(PackageState.VAULT_ADD_VERIFY);
				for (PackageInfo pkgState : cachedList) {
					logger.debug("submitPackageInfo {}", pkgState);
					try {
						pkgState.setStatus(PackageState.VAULT_VERIFY_PASS);
						diamondApi.vaultVerifyResp(pkgState);
						cache.remove(pkgState.getSeqNo(), pkgState.getStatus());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:", e);
						failedList.add(pkgState);
					}
				}
			}
		}
		return failedList;
	}

	@Override
	public boolean vaultAddPackageInfo(PackageInfo pkgInf) {
		logger.debug("vaultAddPackageInfo :{}", pkgInf);
		boolean flag = false;
		String status = pkgInf.getStatus();
		String userid = pkgInf.getUserid();
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		try {
			PackageInfo packageInfoCache =  cache.getPackage(pkgInf.getSeqNo());
			if (status.equals(PackageState.AOC_REQ_VAULT_VERIFY)) {
				packageInfoCache.setVault(pkgInf.getVault());
				packageInfoCache.setOwner(pkgInf.getOwner());
				cache.add(packageInfoCache);
				flag = true;
			} else if (status.equals(PackageState.VAULT_ADD_VERIFY)) {
				packageInfoCache.setInvtymgr(pkgInf.getInvtymgr());
				packageInfoCache.setSealedbagno(pkgInf.getSealedbagno());
				packageInfoCache.setAoc(DiamondApplicationRunner.getAllUserMap().get("AOC"));
				cache.add(packageInfoCache);
				flag = true;
			} 

		} catch (Exception e) {
			logger.error("vaultAddPackageInfo error  :"+pkgInf.getBasketno(), e);
		}
		logger.info("vaultAddPackageInfo {} result: {}",pkgInf.getBasketno(), flag);
		return flag;
	}

	@Override
	public boolean transferPackageInfo(PackageInfo pkgInf) {
		logger.debug("transferPackageInfo :{}", pkgInf);

		boolean flag = false;
		String status = pkgInf.getStatus();
		String userid = pkgInf.getUserid(); 
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		try {
			if (status.equals(PackageState.DMD_REQ_CHG_OWNER)) {
				cache.add(pkgInf);
				flag = true;
			}
		} catch (Exception e) {
			logger.error(" transferPackageInfo error :"+pkgInf.getBasketno(), e);
		}
		logger.info("transferPackageInfo {} success: {}",pkgInf.getBasketno(),flag);
		return flag;
	}

	@Override
	public List<PackageInfo> submitPackageInfo(List<PackageInfo> packageInfos, String step) {
		List<PackageInfo> failedList = new ArrayList<>();
		if (step.equals(Constants.AOC_TO_VAULT_OWNER)) {
			// 16
			// 校验to do
			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("submitPackageInfo {} ", packageInfo);
				try {
					diamondApi.submitChangeOwnerDiamond(packageInfo.getBasketno(), packageInfo.getVault(),packageInfo.getOwner());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}

		} else if (step.equals(Constants.VAULT_OWNER_TO_AOC)) {
			// 17
			// 校验to do
			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("submitPackageInfo {}", packageInfo.getBasketno());
				try {
					diamondApi.changeOwnerResp(packageInfo.getBasketno(), packageInfo.getAoc());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}
		} else if (step.equals(Constants.AOC_TO_AUDIT)) {
			// 校验to do
			String auditor = DiamondApplicationRunner.getAllUserMap().get("AuditorA");
			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("submitPackageInfo {}", packageInfo.getBasketno());
				try {
					diamondApi.auditDiamond(auditor, packageInfo.getBasketno());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}
		} else if (step.equals(Constants.AUDIT_TO_AOC)) {
			// 校验to do

			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("submitPackageInfo {}", packageInfo.getBasketno());
				String status = "";
				if ("verified".equals(packageInfo.getResult())) {
					status = PackageState.AUDIT_VERIFY_PASS;
				} else if ("failure".equals(packageInfo.getResult())) {
					status = PackageState.AUDIT_VERIFY_NOPASS;
				}
				try {
					diamondApi.auditDiamondResp(packageInfo.getBasketno(), packageInfo.getAoc(),
							packageInfo.getAuditdate(), status, packageInfo.getResult());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}
		}
		return failedList;
	}

	@Override
	public boolean auditPackageInfo(PackageInfo packageInfo) {
		logger.debug("auditPackageInfo :{}", JSON.toJSONString(packageInfo));

		boolean flag = false;
		String status = packageInfo.getStatus();
		String userid= packageInfo.getUserid();
		
		try {
			PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
			PackageInfo packageInfoCache =  cache.getPackage(packageInfo.getSeqNo());
			if (status.equals(PackageState.AUDIT_ADD_VERIFY)) {
				packageInfoCache.setResult(packageInfo.getResult());
				packageInfoCache.setAuditdate(packageInfo.getAuditdate());
				cache.add(packageInfoCache);
				flag = true;
			}
		} catch (Exception e) {
			logger.error("auditPackageInfo error "+ packageInfo.getBasketno(), e);
		}
		logger.info("auditPackageInfo result: {}",flag);
		return flag;
	}

	@Override
	public List<PackageAndDiamond> getPackageAndDiamondById(String... basketNo) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(basketNo);
		List<PackageAndDiamond> plist = Lists.newArrayList();
		if (AOCBeanUtils.isNotEmpty(list)) {
			plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
		}
		return plist;
	}

	@Override
	public List<PackageInfo> getPackageStateWithoutRedeemByStatus(String userid,String redeemOwnerId, String... status) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateWithoutRedeemByStatus(redeemOwnerId, status);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		List<PackageInfo> pkgList = cache.getPackageByStatus(status);
	
		logger.debug("userid: {} get {} from corda size: {}", userid, Arrays.toString(status), padList.size());
		logger.debug("userid: {} get {} from cache size: {}", userid, Arrays.toString(status), pkgList.size());
		List<PackageInfo> mergeList = AOCBeanUtils.mergePackageList(pkgList, padList);
		return mergeList;
	}

}
