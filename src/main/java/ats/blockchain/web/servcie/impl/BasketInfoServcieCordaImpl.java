package ats.blockchain.web.servcie.impl;

import java.util.ArrayList;
import java.util.List;

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
import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
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
	public boolean addPackageInfo(PackageInfo pkgInf) {
		logger.debug("addBasketInfo :{}", JSON.toJSONString(pkgInf));
		PackageState pkg = new PackageState();
		BeanUtils.copyProperties(pkgInf, pkg);

		boolean flag = false;
		List<PackageState> list = getPackageStateById(pkgInf.getBasketno());
		String rs = "";
		if (list != null && list.size() > 0) {
			logger.info("basketno exist,set status to remove first :{}", pkgInf.getBasketno());
			try {
				rs = diamondApi.createPackage(pkgInf.getSuppliercode(), pkg, PackageState.PKG_REMOVE);
				logger.info("removeBasketInfo result: {}", rs);
				flag = true;
			} catch (DiamondWebException e) {
				logger.error("removeBasketInfo error ", e);
				return flag;
			}
		}
		try {
			rs = diamondApi.createPackage(pkgInf.getSuppliercode(), pkg, PackageState.PKG_CREATE);
			logger.info("addBasketInfo result: {}", rs);
			flag = true;
		} catch (DiamondWebException e) {
			logger.error("addBasketInfo error ", e);
		}
		return flag;
	}

	@Override
	public List<PackageInfo> getPackageInfo() {

		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		logger.error("getPackageInfo unimplements!!!");

		return pkgList;
	}
	@Override
	public List<PackageInfo> submitPackageByStatus(String status) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageInfo> failedList =new ArrayList<>();
		
		for(StateAndRef<PackageState> state :list) {
			PackageState pkgState = state.getState().getData();
			logger.debug("submitPackage {}",pkgState.getBasketno());
			try {
				diamondApi.createPackage(pkgState.getSuppliercode().getName().toString(), pkgState, PackageState.PKG_ISSUE);
			} catch (DiamondWebException e) {
				logger.error("issue package error:",e);
				PackageAndDiamond pad = AOCBeanUtils.convertSinglePkgState2PkgInfo(state);
				if(pad!=null) {
					failedList.add(pad.getPkgInfo());
				}
			}
		}
		
		return failedList;
	}
	

	@Override
	public List<PackageInfo> getPackageInfoByStatus(String... status) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		for (PackageAndDiamond pad : padList) {
			pkgList.add(pad.getPkgInfo());
		}
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
	public boolean labConfirmPackageInfo(PackageInfo pkgInf)
	{
		logger.debug("confrimPackageInfo :{}", JSON.toJSONString(pkgInf));
		
		boolean flag = false;
		String rs = "";
		String externalid = pkgInf.getBasketno();
		String status = pkgInf.getStatus();
		String lab = pkgInf.getGradlab();
		try {
			if(status.equals(PackageState.AOC_REQ_LAB_VERIFY))
			{
				rs = diamondApi.reqLabVerifyDiamond(externalid, status, lab);
			}else if(status.equals(PackageState.LAB_ADD_VERIFY))
			{
				pkgInf.setAoc(DiamondApplicationRunner.getAllUserMap().get("AOC"));
				pkgInf.setGiacontrolno("123456");
				rs = diamondApi.labVerifyResp(pkgInf);
			}else 
			{
				return flag;
			}
				
			logger.info("confrimPackageInfo result: {}", rs);
			flag = true;
		} catch (DiamondWebException e) {
			logger.error("confrimPackageInfo error ", e);
		}
		return flag;
	}

	@Override
	public List<PackageInfo> submitPackageInfo(String step)
	{
		List<StateAndRef<PackageState>> list = new ArrayList<StateAndRef<PackageState>>();
		List<PackageInfo> failedList =new ArrayList<>();
		
		if(step.equals(Constants.AOC_TO_GIA))
		{
			list = diamondApi.getPackageStateByStatus(PackageState.DMD_ISSUE);
			if(AOCBeanUtils.isNotEmpty(list))
			{
				//校验未add的数据
			}else
			{
				list = diamondApi.getPackageStateByStatus(PackageState.AOC_REQ_LAB_VERIFY);
				for(StateAndRef<PackageState> state :list) {
					PackageState pkgState = state.getState().getData();
					logger.debug("submitPackageInfo {}",pkgState.getBasketno());
					try {
						diamondApi.reqLabVerifyDiamond(pkgState.getBasketno(), PackageState.AOC_SUBMIT_LAB_VERIFY, pkgState.getGradlab().toString());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:",e);
						PackageAndDiamond pad = AOCBeanUtils.convertSinglePkgState2PkgInfo(state);
						if(pad!=null) {
							failedList.add(pad.getPkgInfo());
						}
					}
				}
			}
		}else if(step.equals(Constants.GIA_TO_AOC))
		{
			list = diamondApi.getPackageStateByStatus(PackageState.AOC_SUBMIT_LAB_VERIFY);
			if(AOCBeanUtils.isNotEmpty(list))
			{
				//校验未add的数据
			}else
			{
				list = diamondApi.getPackageStateByStatus(PackageState.LAB_ADD_VERIFY);
				for(StateAndRef<PackageState> state :list) {
					PackageState pkgState = state.getState().getData();
					logger.debug("submitPackageInfo {}",pkgState.getBasketno());
					PackageAndDiamond pad = AOCBeanUtils.convertSinglePkgState2PkgInfo(state);
					try {
						if("verified".equals(pad.getPkgInfo().getResult()))
						{
							pad.getPkgInfo().setStatus(PackageState.LAB_VERIFY_PASS);
						}else if("failure".equals(pad.getPkgInfo().getResult()))
						{
							pad.getPkgInfo().setStatus(PackageState.LAB_VERIFY_NOPASS);
						}
						diamondApi.labVerifyResp(pad.getPkgInfo());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:",e);
						if(pad!=null) {
							failedList.add(pad.getPkgInfo());
						}
					}
				}
			}
		}else if(step.equals(Constants.AOC_TO_VAULT))
		{
			list = diamondApi.getPackageStateByStatus(PackageState.LAB_VERIFY_PASS);
			if(AOCBeanUtils.isNotEmpty(list))
			{
				//校验未add的数据
			}else
			{
				list = diamondApi.getPackageStateByStatus(PackageState.AOC_REQ_VAULT_VERIFY);
				for(StateAndRef<PackageState> state :list) {
					PackageState pkgState = state.getState().getData();
					logger.debug("submitPackageInfo {}",pkgState.getBasketno());
					PackageAndDiamond pad = AOCBeanUtils.convertSinglePkgState2PkgInfo(state);
					try {
						diamondApi.reqVaultVerifyDiamond(pkgState.getBasketno(), PackageState.AOC_SUBMIT_VAULT_VERIFY, pkgState.getVault().toString(), pkgState.getOwner());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:",e);
						if(pad!=null) {
							failedList.add(pad.getPkgInfo());
						}
					}
				}
			}
		}else if(step.equals(Constants.VAULT_TO_AOC))
		{
			list = diamondApi.getPackageStateByStatus(PackageState.AOC_SUBMIT_VAULT_VERIFY);
			if(AOCBeanUtils.isNotEmpty(list))
			{
				//校验未add的数据
				for(StateAndRef<PackageState> state :list) {
					PackageState pkgState = state.getState().getData();
					logger.debug("submitPackageInfo {}",pkgState.getBasketno());
					PackageAndDiamond pad = AOCBeanUtils.convertSinglePkgState2PkgInfo(state);
					failedList.add(pad.getPkgInfo());
				}
			}else
			{
				list = diamondApi.getPackageStateByStatus(PackageState.VAULT_ADD_VERIFY);
				for(StateAndRef<PackageState> state :list) {
					PackageState pkgState = state.getState().getData();
					logger.debug("submitPackageInfo {}",pkgState.getBasketno());
					PackageAndDiamond pad = AOCBeanUtils.convertSinglePkgState2PkgInfo(state);
					try {
						pad.getPkgInfo().setStatus(PackageState.VAULT_VERIFY_PASS);
						diamondApi.vaultVerifyResp(pad.getPkgInfo());
					} catch (DiamondWebException e) {
						logger.error("submitPackageInfo error:",e);
						if(pad!=null) {
							failedList.add(pad.getPkgInfo());
						}
					}
				}
			}
		}
		return failedList;
	}

	
	
	@Override
	public boolean vaultAddPackageInfo(PackageInfo pkgInf)
	{
		logger.debug("vaultAddPackageInfo :{}", JSON.toJSONString(pkgInf));
		
		boolean flag = false;
		String rs = "";
		String externalid = pkgInf.getBasketno();
		String status = pkgInf.getStatus();
		String vault = pkgInf.getVault();
		String owner = pkgInf.getOwner();
		try {
			if(status.equals(PackageState.AOC_REQ_VAULT_VERIFY))
			{
				rs = diamondApi.reqVaultVerifyDiamond(externalid, status, vault, owner);
			}else if(status.equals(PackageState.VAULT_ADD_VERIFY))
			{
				pkgInf.setAoc(DiamondApplicationRunner.getAllUserMap().get("AOC"));
				rs = diamondApi.vaultVerifyResp(pkgInf);
			}else 
			{
				return flag;
			}
				
			logger.info("confrimPackageInfo result: {}", rs);
			flag = true;
		} catch (DiamondWebException e) {
			logger.error("confrimPackageInfo error ", e);
		}
		return flag;
	}

	@Override
	public boolean transferPackageInfo(PackageInfo pkgInf)
	{
		logger.debug("vaultAddPackageInfo :{}", JSON.toJSONString(pkgInf));
		
		boolean flag = false;
		String rs = "";
		String externalid = pkgInf.getBasketno();
		String status = pkgInf.getStatus();
		String vault = pkgInf.getVault();
		String owner = pkgInf.getOwner();
		try {
			if(status.equals(PackageState.DMD_REQ_CHG_OWNER))
			{
				rs = diamondApi.reqChangeOwnerDiamond(externalid, vault, owner);
			}else 
			{
				return flag;
			}
				
			logger.info("confrimPackageInfo result: {}", rs);
			flag = true;
		} catch (DiamondWebException e) {
			logger.error("confrimPackageInfo error ", e);
		}
		return flag;
	}

	@Override
	public List<PackageInfo> submitPackageInfo(List<PackageInfo> packageInfos,String step)
	{
		List<PackageInfo> failedList =new ArrayList<>();
		if(step.equals(Constants.AOC_TO_VAULT_OWNER))
		{
			//16
			//校验to do
			for(PackageInfo packageInfo : packageInfos)
			{
				logger.debug("submitPackageInfo {}",packageInfo.getBasketno());
				try
				{
					diamondApi.submitChangeOwnerDiamond(packageInfo.getBasketno(), packageInfo.getVault());
				} catch (DiamondWebException e)
				{
					logger.error("submitPackageInfo error:",e);
					failedList.add(packageInfo);
				}
			}
			
			
		}else if(step.equals(Constants.VAULT_OWNER_TO_AOC))
		{
			//17
			//校验to do
			for(PackageInfo packageInfo : packageInfos)
			{
				logger.debug("submitPackageInfo {}",packageInfo.getBasketno());
				try
				{
					diamondApi.changeOwnerResp(packageInfo.getBasketno(), packageInfo.getAoc());
				} catch (DiamondWebException e)
				{
					logger.error("submitPackageInfo error:",e);
					failedList.add(packageInfo);
				}
			}
		}else if(step.equals(Constants.AOC_TO_AUDIT))
		{
			//校验to do
			String auditor = DiamondApplicationRunner.getAllUserMap().get("AuditorA");
			for(PackageInfo packageInfo : packageInfos)
			{
				logger.debug("submitPackageInfo {}",packageInfo.getBasketno());
				try
				{
					diamondApi.auditDiamond(auditor, packageInfo.getBasketno());
				} catch (DiamondWebException e)
				{
					logger.error("submitPackageInfo error:",e);
					failedList.add(packageInfo);
				}
			}
		}else if(step.equals(Constants.AUDIT_TO_AOC))
		{
			//校验to do
			
			for(PackageInfo packageInfo : packageInfos)
			{
				logger.debug("submitPackageInfo {}",packageInfo.getBasketno());
				String status = "";
				if("verified".equals(packageInfo.getResult()))
				{
					status = PackageState.AUDIT_VERIFY_PASS;
				}else if("failure".equals(packageInfo.getResult()))
				{
					status = PackageState.AUDIT_VERIFY_NOPASS;
				}
				try
				{
					diamondApi.auditDiamondResp(packageInfo.getBasketno(), packageInfo.getAoc(), packageInfo.getAuditdate(), status, packageInfo.getResult());
				} catch (DiamondWebException e)
				{
					logger.error("submitPackageInfo error:",e);
					failedList.add(packageInfo);
				}
			}
		}
		return failedList;
	}

	@Override
	public boolean auditPackageInfo(PackageInfo packageInfo)
	{
		logger.debug("auditPackageInfo :{}", JSON.toJSONString(packageInfo));
		
		boolean flag = false;
		String rs = "";
		String externalid = packageInfo.getBasketno();
		String auditor = DiamondApplicationRunner.getAllUserMap().get("AuditorA");
		String aoc = DiamondApplicationRunner.getAllUserMap().get("AOC");
		String status = packageInfo.getStatus();
		try {
			if(status.equals(PackageState.AUDIT_ADD_VERIFY))
			{
				rs = diamondApi.auditDiamondResp(externalid, aoc, packageInfo.getAuditdate(), status, packageInfo.getResult());
			}else 
			{
				return flag;
			}
				
			logger.info("auditPackageInfo result: {}", rs);
			flag = true;
		} catch (DiamondWebException e) {
			logger.error("auditPackageInfo error ", e);
		}
		return flag;
	}

	@Override
	public List<PackageAndDiamond> getPackageAndDiamondById(String... basketNo)
	{
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(basketNo);
		List<PackageAndDiamond> plist =Lists.newArrayList();
		if(AOCBeanUtils.isNotEmpty(list))
		{
			plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
		}
		return plist;
	}

	@Override
	public List<PackageInfo> getPackageStateWithoutRedeemByStatus(String redeemOwnerId, String... status)
	{
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateWithoutRedeemByStatus(redeemOwnerId, status);
		List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		List<PackageInfo> pkgList = new ArrayList<PackageInfo>();
		for (PackageAndDiamond pad : padList) {
			pkgList.add(pad.getPkgInfo());
		}
		return pkgList;
	}
}
