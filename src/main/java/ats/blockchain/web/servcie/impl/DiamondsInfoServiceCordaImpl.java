package ats.blockchain.web.servcie.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import ats.blockchain.web.cache.CacheFactory;
import ats.blockchain.web.cache.DiamondCache;
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
 * @author shi hongyu
 *
 */
@Service
public class DiamondsInfoServiceCordaImpl implements DiamondsInfoService {
	@Autowired
	private CordaApi cordaApi;
	private DiamondTradeApi diamondApi;
	private static Logger logger = LoggerFactory.getLogger(DiamondsInfoServiceCordaImpl.class);

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
		String basketno = di.getBasketno();
		String userid = di.getUserid();
		logger.debug("addDiamondInfo aoc: {}, basketNo: {} ,userid:{}", aocLegalName, basketno, userid);
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		String giano = di.getGiano();
		if (cache.containsDiamond(giano)) {
			logger.error("addDiamondInfo:giano duplicate. giano : {} ,aoc: {}, basketNo: {} ,userid:{}", giano,
					aocLegalName, basketno, userid);
			return ResultUtil.failMap("giano:" + giano + " duplicate.");
		}

		try {
			logger.debug("addDiamondInfo to package : {}", di);
			cache.add(di);
			return ResultUtil.msgMap(true, "success");
		} catch (Exception e) {
			logger.error("addDiamondInfo basketNo " + basketno + " error:", e);
			String message = e.getMessage();
			String err = message.substring(message.indexOf(':') + 1);
			return ResultUtil.failMap(err);
		}
	}

	@Override
	public Map<String, Object> editDiamondInfo(DiamondInfoData di) {
		logger.debug("editDiamondInfo: {}", di);
		String aocLegalName = DiamondApplicationRunner.getAllUserMap().get("AOC");
		if (aocLegalName == null) {
			logger.error("can't get aoc userinfo,won't edit diamondInfo {}", di.getBasketno());
			return ResultUtil.failMap("can't get AOC info.");
		}
		String basketno = di.getBasketno();
		String userid = di.getUserid();
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		try {
			logger.debug("editDiamondInfo to package : {}", di);
			cache.update(di);
			return ResultUtil.msgMap(true, "success");
		} catch (Exception e) {
			logger.error("editDiamondInfo basketNo " + basketno + " error:", e);
			String message = e.getMessage();
			String err = message.substring(message.indexOf(':') + 1);
			return ResultUtil.failMap(err);
		}

	}

	@Override
	public Map<String, Object> deleteDiamondInfo(DiamondInfoData di) {
		logger.debug("deleteDiamondInfo: {}", di);
		String basketno = di.getBasketno();
		String userid = di.getUserid();
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		try {
			logger.debug("deleteDiamondInfo to package : {}", di);
			cache.removeDiamond(basketno, di.getStatus(), di.getTradeid());
			return ResultUtil.msgMap(true, "success");
		} catch (Exception e) {
			logger.error("deleteDiamondInfo basketNo " + basketno + " error:", e);
			String message = e.getMessage();
			String err = message.substring(message.indexOf(':') + 1);
			return ResultUtil.failMap(err);
		}
	}

	@Override
	public List<DiamondInfoData> getDiamondInfoByStatus(String userid, String... status) {
		logger.debug("getDiamondInfoByStatus ,userid: {} ,status: {}", userid, Arrays.toString(status));
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateByStatus(status);
		List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		plist.forEach(p ->cache.add(p));
		List<PackageAndDiamond> cacheList = cache.getDiamondByStatus(status);

		List<DiamondInfoData> dList = Lists.newArrayList();
		cacheList.stream().forEach(p -> {
			PackageInfo pkg = p.getPkgInfo();
			int num = pkg.getDiamondsnumber();
			int addednum = null == p.getDiamondList() ? 0 : p.getDiamondList().size();

			logger.debug("convert PackageInfo to DiamondInfo, basketno: {}, size: {}", pkg.getBasketno(), num);
			for (int i = addednum; i < num; i++) {
				DiamondInfoData di = new DiamondInfoData();
				BeanUtils.copyProperties(pkg, di);
				logger.debug("copy packagInfo: {} ,diamondInfo: {}", pkg, di);
				dList.add(di);
			}
			if (null != p.getDiamondList()) {
				dList.addAll(p.getDiamondList());
			}
		});

		return dList;
	}


	/**
	 * add by shuhao.song 2018-9-26 14:31:12
	 */
	@Override
	public List<DiamondInfoData> submitDiamondList(String userid) {
		String aocLegalName = DiamondApplicationRunner.getAllUserMap().get("AOC");
		if (aocLegalName == null) {
			logger.error("submitDiamondList aocLegalName is null");
		}
		List<DiamondInfoData> dList = Lists.newArrayList();
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		List<PackageAndDiamond> plist =	cache.getDiamondByStatus(PackageState.PKG_ISSUE);
		if (AOCBeanUtils.isNotEmpty(plist)) {
			plist.stream().forEach(p -> {
				PackageInfo pkg = p.getPkgInfo();
				DiamondInfoData di = new DiamondInfoData();
				BeanUtils.copyProperties(pkg, di);
				logger.debug("copy packagInfo: {} ,diamondInfo: {}", pkg, di);
				dList.add(di);
			});
			return dList;
		} else {
			plist = cache.getDiamondByStatus(PackageState.DMD_CREATE);
			logger.debug("submitDiamondList package size: {}",plist.size());
			for (PackageAndDiamond packageAndDiamond : plist) {
				PackageInfo pkgInfo = packageAndDiamond.getPkgInfo();
				String basketno = pkgInfo.getBasketno();
				List<DiamondInfoData> dl = packageAndDiamond.getDiamondList();
				logger.debug("submitDiamondList basketno: {} ,diamond size : {}",basketno,dl.size());
				List<DiamondsInfo> diList =Lists.newArrayListWithCapacity(dl.size());
				dl.forEach(d -> {
					DiamondsInfo di = new DiamondsInfo();
					BeanUtils.copyProperties(d, di);
					diList.add(di);
				});
				try {
					diamondApi.issueDiamond(aocLegalName, basketno,diList);
					String status = pkgInfo.getStatus();
					cache.remove(basketno, status);
				} catch (DiamondWebException e) {
					logger.error("submitDiamondList error:Basketno:" + basketno, e);
					dList.addAll(dl);
				}
			}
		}
		return dList;

	}

	@Override
	public List<DiamondInfoData> getDiamondInfoData() {
		List<StateAndRef<PackageState>> list = diamondApi.getAllPackageState();
		List<DiamondInfoData> dList = Lists.newArrayList();
		if (AOCBeanUtils.isNotEmpty(list)) {
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.stream().forEach(p -> {
				logger.debug("copy diamondInfo: {}", p.getDiamondList());
				if (p.getDiamondList() != null)
					dList.addAll(p.getDiamondList());
			});
		}
		return dList;
	}

	@Override
	public List<DiamondInfoData> getDiamondInfoHistory(String giano, String basketno) {
		List<StateAndRef<PackageState>> list = diamondApi.getPackageStateById(Vault.StateStatus.ALL, basketno);
		List<DiamondInfoData> diamondInfoDatas = Lists.newArrayList();
		if (AOCBeanUtils.isNotEmpty(list)) {
			List<PackageAndDiamond> plist = AOCBeanUtils.convertPakageState2PackageInfo(list);
			plist.forEach(p -> {
				if (null != p.getDiamondList()) {
					p.getDiamondList().stream().forEach(item -> {
						item.setStatus(
								ats.blockchain.cordapp.diamond.util.Constants.PKG_STATE_MAP.get(item.getStatus()));
						if (item.getGiano().equals(giano)) {
							diamondInfoDatas.add(item);
						}
					});
				}

			});
		}
		// diamondInfoDatas.addAll(p.getDiamondList().stream().filter(item->item.getGiano().equals(giano)).collect(Collectors.toList()));
		return diamondInfoDatas;
	}

	/**
	 *@return valid :true Giano不重复，false Giano重复
	 */
	@Override
	public Map<String, Object> checkGiano(String userid, String tradeid, String giano) {
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		Map<String, Object> rs = new HashMap<String, Object>();
		boolean flag = false;
		if (StringUtils.isNotBlank(tradeid)) {
			try {
				boolean isChange = cache.checkGianoChange(tradeid, giano);
				logger.debug("checkGiano ,tradeid: {} ,giano: {} ",tradeid, giano);
				flag = isChange ? !cache.containsDiamond(giano) : true;
			} catch (DiamondWebException e) {
				rs.put("valid", false);
				rs.put("message", e.getMessage());
				logger.warn("checkGiano", e);
			}
		} else {
			flag = !cache.containsDiamond(giano);
		}
		rs.put("valid", flag);
		logger.debug("checkGiano ,tradeid: {} ,giano:{} ,result: {}",tradeid, giano,flag);
		return rs;
	}

}
