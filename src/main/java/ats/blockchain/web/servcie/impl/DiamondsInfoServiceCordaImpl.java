package ats.blockchain.web.servcie.impl;

import java.util.List;
import java.util.Map;

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
import ats.blockchain.web.utils.ResultUtil;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.node.services.Vault;


/**
 * 
 * @author Administrator
 *
 */
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
	public Map<String, Object> addDiamondInfo(DiamondInfoData di) {
		logger.debug("addDiamondInfo: {}", di);
		String aocLegalName = DiamondApplicationRunner.getAllUserMap().get("AOC");
		if (aocLegalName == null) {
			logger.error("can't get aoc userinfo,won't add diamondInfo {}", di.getBasketno());
			return ResultUtil.failMap("can't get AOC info.");
		}
		DiamondsInfo di1 = new DiamondsInfo();
		BeanUtils.copyProperties(di, di1);
		String id = di.getBasketno();
		logger.debug("addDiamondInfo aoc: {}, basketNo: {} {}", aocLegalName, di1);
		try {
			String rs = diamondApi.createDiamond(aocLegalName, id, Lists.newArrayList(di1));
			logger.debug("addDiamondInfo aoc: {}, basketNo: {},result: {}", aocLegalName, id, rs);
			return ResultUtil.msgMap(true, "success");
		} catch (DiamondWebException e) {
			logger.error("addDiamondInfo basketNo " + id + " error:", e);
			String message = e.getMessage();
			String err = message.substring(message.indexOf(':')+1);
			return ResultUtil.failMap(err);
		}
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

	/**
	 * add by shuhao.song
	 * 2018-9-26 14:31:12
	 */
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
	
	@Override
	public List<DiamondInfoData> getDiamondInfoData()
	{
		List<StateAndRef<PackageState>> list = diamondApi.getAllPackageState();
		List<DiamondInfoData> dList = Lists.newArrayList();
		if(AOCBeanUtils.isNotEmpty(list))
		{
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.stream().forEach(p -> {
				logger.debug("copy diamondInfo: {}",p.getDiamondList());
				if(p.getDiamondList()!=null)
				dList.addAll(p.getDiamondList());
			});
		}
		return dList;
	}

	@Override
	public List<DiamondInfoData> getDiamondInfoHistory(String giano,String basketno)
	{
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(Vault.StateStatus.ALL, basketno);
		List<DiamondInfoData> diamondInfoDatas = Lists.newArrayList();
		if(AOCBeanUtils.isNotEmpty(list))
		{
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.forEach(p->{
				if(null != p.getDiamondList())
				{
					p.getDiamondList().stream().forEach(
							item->{item.setStatus(ats.blockchain.cordapp.diamond.util.Constants.PKG_STATE_MAP.get(item.getStatus()));
							if(item.getGiano().equals(giano))
							{
								diamondInfoDatas.add(item);
							}
						}
					);
				}

			});
		}
		//diamondInfoDatas.addAll(p.getDiamondList().stream().filter(item->item.getGiano().equals(giano)).collect(Collectors.toList()));
		return diamondInfoDatas;
	}

}
