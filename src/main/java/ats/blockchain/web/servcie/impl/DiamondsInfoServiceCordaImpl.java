package ats.blockchain.web.servcie.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.servcie.DiamondsInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import net.corda.core.contracts.StateAndRef;

@Service
public class DiamondsInfoServiceCordaImpl implements DiamondsInfoService {
	@Autowired
	private CordaApi cordaApi;
	private DiamondTradeApi diamondApi;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@PostConstruct
	public void init() {
		diamondApi = cordaApi.getTradediamondinf();
	}

	@Override
	public boolean addDiamondInfo(DiamondInfoData di) {
		logger.debug("addDiamondInfo: {}", di);
		String aocLegalName = DiamondApplicationRunner.getAllUserMap().get("AOC");
		if (aocLegalName == null) {
			logger.error("can't get aoc userinfo,won't add diamondInfo {}", di.getBasketno());
			return false;
		}
		DiamondsInfo di1 = new DiamondsInfo();
		BeanUtils.copyProperties(di, di1);
		String id = di.getBasketno();
		logger.debug("addDiamondInfo aoc: {}, basketNo: {} {}", aocLegalName, di1);
		try {
			String rs = diamondApi.createDiamond(aocLegalName, id, Collections.singletonList(di1));
			logger.debug("addDiamondInfo aoc: {}, basketNo: {},result: {}", aocLegalName, id, rs);
			return true;
		} catch (DiamondWebException e) {
			logger.error("addDiamondInfo basketNo " + id + " error:", e);
		}
		return false;
	}

	@Override
	public List<DiamondInfoData> getDiamondInfoByStatus(String... status) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
		List<DiamondInfoData> dList = Lists.newArrayList();
		plist.stream().forEach(p -> {
			PackageInfo pkg = p.getPkgInfo();
			int num = pkg.getDiamondsnumber();
			int addednum = null == p.getDiamondList() ? 0 : p.getDiamondList().size(); 
			
			logger.debug("convert PackageInfo to DiamondInfo, basketno: {}, size: {}",pkg.getBasketno(),num);
			for(int i = addednum; i<num; i++)
			{
				DiamondInfoData di = new DiamondInfoData();
				BeanUtils.copyProperties(pkg, di);
				logger.debug("copy packagInfo: {} ,diamondInfo: {}",pkg,di);
				dList.add(di);
			}
			if(null != p.getDiamondList()) 
			{
				dList.addAll(p.getDiamondList());
			}
		});

		return dList;
	}

	@Override
	public List<DiamondInfoData> submitDiamondList() {
		String aocLegalName = DiamondApplicationRunner.getAllUserMap().get("AOC");
		if (aocLegalName == null) {
			logger.error("submitDiamondList aocLegalName is null");
		}
		List<DiamondInfoData> dList = Lists.newArrayList();
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(PackageState.PKG_ISSUE);
		if(AOCBeanUtils.isNotEmpty(list))
		{
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.stream().forEach(p -> {
				PackageInfo pkg = p.getPkgInfo();
				DiamondInfoData di = new DiamondInfoData();
				BeanUtils.copyProperties(pkg, di);
				logger.debug("copy packagInfo: {} ,diamondInfo: {}",pkg,di);
				dList.add(di);
			});
			return dList;
		}else
		{
			list = diamondApi.getPackageStateByStatus(PackageState.DMD_CREATE);
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			for(PackageAndDiamond packageAndDiamond : plist)
			{
				try
				{
					diamondApi.issueDiamond(aocLegalName, packageAndDiamond.getPkgInfo().getBasketno());
				} catch (DiamondWebException e)
				{
					logger.error("submitDiamondList error:Basketno:{}",packageAndDiamond.getPkgInfo().getBasketno());
				}
			}
			
		}
		return dList;
		
	}

}
