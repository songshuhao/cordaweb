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

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import net.corda.core.contracts.StateAndRef;

@Service
public class BasketInfoServcieCordaImpl implements PackageInfoService {
	@Autowired
	private CordaApi cordaApi;
	private DiamondTradeApi diamondApi;
	// private Logger logger = LoggerFactory.getLogger(getClass());
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
}
