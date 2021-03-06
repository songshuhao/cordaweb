package ats.blockchain.web.servcie.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import ats.blockchain.web.utils.Base64Utils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.ResultUtil;
import ats.blockchain.web.utils.StringUtil;
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
		String basketno = pkgInf.getBasketno();
		logger.debug("get package cache of user: {}", userid);
		if(userid ==null) {
			logger.error("userid is null ,basketno: {}",basketno);
			return ResultUtil.failMap("userid can't be null");
		}
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		boolean flag = cache.containsPackage(pkgInf.getBasketno());
		if (flag) {
			return ResultUtil.failMap("package no duplicate :" + basketno);
		}
		pkgInf.setStatus(PackageState.PKG_CREATE);
		String  seqNo = StringUtil.getPackageSeqno();
		pkgInf.setSeqNo(seqNo);
		pkgInf.setStatusDesc(ats.blockchain.cordapp.diamond.util.Constants.PKG_STATE_MAP.get(PackageState.PKG_CREATE));
		cache.add(pkgInf);
		logger.debug("{} package add to cache.", basketno);
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
		List<StateAndRef<PackageState>> list = diamondApi.getAllPackageState();
		logger.debug("getPackageInfo size: {}",list.size());
		if (AOCBeanUtils.isNotEmpty(list)) {
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.stream().forEach(p -> {
				logger.debug("get getPkgInfo: {}", p.getPkgInfo());
					pkgList.add(p.getPkgInfo());
			});
		}
		return pkgList;
	}
	
	@Override
	public long getPackageInfoNum() {
		List<StateAndRef<PackageState>> list = diamondApi.getAllPackageState();
		return (long) list.size();
	}
	
	@Override
	public List<PackageInfo> getPackageInfo(int pageNum, int pageSize) {
		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		List<StateAndRef<PackageState>> list = diamondApi.getAllPackageState(pageNum,pageSize);
		logger.debug("getPackageInfo size: {}",list.size());
		if (AOCBeanUtils.isNotEmpty(list)) {
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.stream().forEach(p -> {
				logger.debug("get getPkgInfo: {}", p.getPkgInfo());
					pkgList.add(p.getPkgInfo());
			});
		}
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
		padList.forEach( p -> cache.update(p.getPkgInfo()));
		
		List<PackageInfo> pkgList = cache.getPackageByStatus(status);
		return pkgList;
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
	public List<PackageInfo> getPackageInfoById(int pageNum, int pageSize,String... basketNo) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(basketNo);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		for (PackageAndDiamond pad : padList) {
			pkgList.add(pad.getPkgInfo());
		}
		return pkgList;
	}

	@Override
	public long getPackageInfoNumById(String... basketNo) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(basketNo);
		return (long) list.size();
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
		logger.debug("confrimPackageInfo :{}",pkgInf);

		boolean flag = false;
		String rs = "";
		String status = pkgInf.getStatus();
		String userid = pkgInf.getUserid();
		try {
			PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
			if (status.equals(PackageState.AOC_REQ_LAB_VERIFY)) {
				PackageInfo old = cache.getPackage(pkgInf.getSeqNo());
				old.setGradlab(pkgInf.getGradlab());
				old.setStatus(status);
			} else if (status.equals(PackageState.LAB_ADD_VERIFY)) {
				PackageInfo old = cache.getPackage(pkgInf.getSeqNo());
				old.setResult(pkgInf.getResult());
				old.setReverification(pkgInf.getReverification());
				old.setGiaapproveddate(pkgInf.getGiaapproveddate());
				old.setStatus(status);
				//old.setGiacontrolno("1");
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
				failedList.addAll(pkgStateList);
			} else {
				cachedList = cache.getPackageByStatus(PackageState.AOC_REQ_LAB_VERIFY);
				for (PackageInfo pkgState : cachedList) {
					logger.debug("submitPackageInfo {}", pkgState.getBasketno());
					try {
						diamondApi.reqLabVerifyDiamond(pkgState.getBasketno(),  pkgState.getStatus(),
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
				failedList.addAll(pkgStateList);
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
				logger.debug("submitPackageInfo {}", pkgStateList);
				failedList.addAll(pkgStateList);
			} else {
				cachedList = cache.getPackageByStatus(PackageState.AOC_REQ_VAULT_VERIFY);
				for (PackageInfo pkgState : cachedList) {
					logger.debug("submitPackageInfo {} {} ", pkgState.getBasketno(),pkgState.getStatusDesc(),pkgState.getOwner());
					try {
						diamondApi.reqVaultVerifyDiamond(pkgState.getBasketno(), PackageState.AOC_SUBMIT_VAULT_VERIFY,
								pkgState.getVault().toString(),Base64Utils.encode(pkgState.getOwner()));
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
				packageInfoCache.setStatus(status);
				flag = true;
			} else if (status.equals(PackageState.VAULT_ADD_VERIFY)) {
				packageInfoCache.setInvtymgr(pkgInf.getInvtymgr());
				packageInfoCache.setSealedbagno(pkgInf.getSealedbagno());
				packageInfoCache.setAoc(DiamondApplicationRunner.getAllUserMap().get("AOC"));
				packageInfoCache.setStatus(status);
				flag = true;
			} else {
				return flag;
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
				PackageInfo old = cache.getPackage(pkgInf.getSeqNo());
				old.setOwner(pkgInf.getOwner());
				old.setVault(pkgInf.getVault());
				old.setStatus(status);
				old.setIsChange("true");
				flag = true;
			}
		} catch (Exception e) {
			logger.error(" transferPackageInfo error :"+pkgInf.getBasketno(), e);
		}
		logger.info("transferPackageInfo {} success: {}",pkgInf.getBasketno(),flag);
		return flag;
	}

	@Override
	public List<PackageInfo> submitPackageInfo(List<PackageInfo> packageInfos, String step,String userid) {
		logger.debug("submitPackageInfo, step: {} ,num: {}",step,packageInfos.size());
		List<PackageInfo> failedList = new ArrayList<>();
		if (step.equals(Constants.AOC_TO_VAULT_OWNER)) {
			// 16
			// 校验to do
			for (PackageInfo packageInfo : packageInfos) {
				PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
				logger.debug("submit change owner baksetno : {} ,package: {}", packageInfo);
				try {
					diamondApi.submitChangeOwnerDiamond(packageInfo.getBasketno(), packageInfo.getVault(),Base64Utils.encode(packageInfo.getOwner()),packageInfo.getStatus());
					cache.remove(packageInfo.getSeqNo(), packageInfo.getStatus());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}

		} else if (step.equals(Constants.VAULT_OWNER_TO_AOC)) {
			// 17
			// 校验to do
			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("change owner response basketno: {} ,package: {}",packageInfo.getBasketno(), packageInfo);
				PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
				try {
					diamondApi.changeOwnerResp(packageInfo.getBasketno(), packageInfo.getAoc(),packageInfo.getStatus());
					cache.remove(packageInfo.getSeqNo(), packageInfo.getStatus());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}
		} else if (step.equals(Constants.AOC_TO_AUDIT)) {
			// 校验to do
			String auditor = DiamondApplicationRunner.getAllUserMap().get("AuditorA");
			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("submitPackageInfo aoc to audit basketno:{} , package:{}", packageInfo);
				PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
				try {
					diamondApi.auditDiamond(auditor, packageInfo.getBasketno(), packageInfo.getStatus());
					cache.remove(packageInfo.getSeqNo(), packageInfo.getStatus());
				} catch (DiamondWebException e) {
					logger.error("submitPackageInfo error:", e);
					failedList.add(packageInfo);
				}
			}
		} else if (step.equals(Constants.AUDIT_TO_AOC)) {
			// 校验to do

			for (PackageInfo packageInfo : packageInfos) {
				logger.debug("submitPackageInfo audit to aoc basketno: {} ,package:{}", packageInfo.getBasketno(),packageInfo);
				PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
				String oldStatus = packageInfo.getStatus();
			
				String status = "";
				if ("verified".equals(packageInfo.getResult())) {
					status = PackageState.AUDIT_VERIFY_PASS;
				} else if ("failure".equals(packageInfo.getResult())) {
					status = PackageState.AUDIT_VERIFY_NOPASS;
				}
				try {
					diamondApi.auditDiamondResp(packageInfo.getBasketno(), packageInfo.getAoc(),
							packageInfo.getAuditdate(), status, packageInfo.getResult());
					cache.remove(packageInfo.getSeqNo(), oldStatus);
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
				packageInfoCache.setStatus(status);
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
		padList.forEach(p-> cache.update(p.getPkgInfo()));
		List<PackageInfo> pkgList = cache.getPackageByStatus(status);
	
		return pkgList;
	}

	@Override
	public Map<String, Object> checkPackageNo(String userid, String seqno, String basketno) {
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		boolean flag = cache.checkPackagenoChange(seqno, basketno);
		
		flag = flag ? !cache.containsPackage(basketno): true;
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("valid", flag);
		logger.debug("checkPackageNo ,seqNo: {} ,basketno:{} ,result: {}",seqno, basketno,flag);
		return rs;
	}

	@Override
	public List<PackageInfo> getPackageInfoByStatus(String userid, String step, String... status)
	{
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		PackageCache cache = CacheFactory.Instance.getPackageCache(userid);
		padList.forEach( p -> {
			cache.update( p.getPkgInfo());
		});
		
		List<PackageInfo> pkgList = cache.getPackageByStatus(status);
		return pkgList;
	
	}

}
