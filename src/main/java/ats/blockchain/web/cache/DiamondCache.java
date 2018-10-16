package ats.blockchain.web.cache;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.cordapp.diamond.util.Constants;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.utils.StringUtil;

public class DiamondCache {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * row : basketno<br>
	 * column:status <br>
	 * value: PackageAndDiamond
	 */
	private static TableCache<String, String, PackageAndDiamond> diamondCache = new TableCache<>();
	/**
	 * 缓存所有giano 用于校验钻石是否重复
	 */
	private Set<String> diamondSet = null;
	/**
	 * 缓存钻石tradeid 和giano 的对应关系， 用于更新giano时候 将原giano 从diamondSet中移除
	 */
	private Map<String, String> tradeIdGiaMap = new ConcurrentHashMap<String, String>();
	/**
	 * 缓存giano 与 baksetno 的对应关系，用于更新钻石所属篮子的时候将钻石从原篮子中移除
	 */
	private Map<String, String> giaBasketnoMap = new HashMap<String, String>();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public void add(PackageAndDiamond pad) {
		PackageInfo pkg = pad.getPkgInfo();
		String status = pkg.getStatus();
		String basketno = pkg.getBasketno();
		diamondCache.put(basketno, status, pad);
		List<DiamondInfoData> list = pad.getDiamondList();
		if (list == null) {
			return;
		}
		synchronized (diamondSet) {
			list.forEach(p -> {
				String tradeid = p.getTradeid();
				if (StringUtils.isBlank(tradeid)) {
					tradeid = StringUtil.getDiamondSeqno();
				}
				p.setTradeid(tradeid);
				String giano = p.getGiano();
				diamondSet.add(giano);

				updateTradeIdGiaMap(tradeid, giano);
				// updateDiamondInPackage(status, basketno, giano);
			});
		}
	}

	public void add(DiamondInfoData diamond) throws DiamondWebException {
		String status = diamond.getStatus();
		String basketno = diamond.getBasketno();
		PackageAndDiamond pad1 = diamondCache.getValue(basketno, status);
		List<DiamondInfoData> list = pad1.getDiamondList();
		if (list == null) {
			list = Lists.newArrayList();
			pad1.setDiamondList(list);
		}
		String tradeid = StringUtil.getDiamondSeqno();
		diamond.setTradeid(tradeid);
		list.add(diamond);
		
		logger.debug("add diamond  {} to package {}",diamond.getGiano(),pad1.getPkgInfo().getBasketno());
		// pad1.getPkgInfo().setStatusDesc(Constants.PKG_STATE_MAP.get(status));
		synchronized (diamondSet) {
			String giano = diamond.getGiano();
			diamondSet.add(giano);
			updateTradeIdGiaMap(tradeid, giano);
		}
		check(pad1);
		PackageInfo stat = pad1.getPkgInfo();
		int dlSize = list.size();
		int basketSize = stat.getDiamondsnumber();
		if (dlSize == basketSize) {
			String oldStatus = stat.getStatus();
			String newStatus = PackageState.DMD_CREATE;
			stat.setStatus(newStatus);
			stat.setStatusDesc(Constants.PKG_STATE_MAP.get(stat.getStatus()));
			list.stream().forEach(d -> {
				d.setStatus(newStatus);
				d.setStatusDesc(stat.getStatusDesc());
			});

			diamondCache.remove(basketno, oldStatus);
			diamondCache.put(basketno, newStatus, pad1);
			logger.debug("update package: {}", pad1);
		}
	}

	/**
	 * 更新钻石信息
	 * 
	 * @param diamond
	 * @throws DiamondWebException
	 */

	public void update(DiamondInfoData diamond) throws DiamondWebException {
		String status = diamond.getStatus();
		String basketno = diamond.getBasketno();
		PackageAndDiamond oldPad = diamondCache.getValue(basketno, status);
		List<DiamondInfoData> list = oldPad.getDiamondList();
		if (list == null) {
			return;
		}
		synchronized (diamondSet) {
			for (DiamondInfoData p : list) {
				String tradeid = p.getTradeid();
				if (tradeid.equals(diamond.getTradeid())) {
					diamondSet.remove(p.getGiano());
					BeanUtils.copyProperties(diamond, p);
					p.setStatus(status);
					;
					p.setStatusDesc(oldPad.getPkgInfo().getStatusDesc());
					String giano = diamond.getGiano();
					diamondSet.add(giano);
					updateTradeIdGiaMap(tradeid, giano);
					break;
				}
			}
		}
		check(oldPad);
	}

	/**
	 * 删除篮子中的钻石
	 * 
	 * @param basketno
	 * @param status
	 * @param tradeid
	 */
	public void removeDiamond(String basketno, String status, String tradeid) {
		PackageAndDiamond pad = diamondCache.getValue(basketno, status);
		List<DiamondInfoData> diamondList = pad.getDiamondList();
		if (diamondList == null) {
			logger.warn("package {} has no diamond,won't remove diamond.", basketno);
			return;
		}
		List<DiamondInfoData> list = Lists.newArrayList();
		int diamondSize = diamondList.size();
		PackageInfo pkgInfo = pad.getPkgInfo();
		int orginalSize = pkgInfo.getDiamondsnumber();

		synchronized (diamondSet) {
			for (DiamondInfoData d : diamondList) {
				if (d.getTradeid().equals(tradeid)) {
					diamondSet.remove(d.getGiano());
				} else {
					list.add(d);
				}
			}
		}
		pad.setDiamondList(list);

		String pkgIssue = PackageState.PKG_ISSUE;
		String statusDesc = Constants.PKG_STATE_MAP.get(PackageState.PKG_ISSUE);
		if (diamondSize == orginalSize) {
			pkgInfo.setStatus(pkgIssue);
			pkgInfo.setStatusDesc(statusDesc);

			list.forEach(d -> {
				d.setStatus(pkgIssue);
				d.setStatusDesc(statusDesc);
			});

			diamondCache.remove(basketno, status);
			diamondCache.put(basketno, pkgIssue, pad);
		}

	}

	public PackageAndDiamond getDiamond(String basketno, String status) {
		return diamondCache.getValue(basketno, status);
	}

	public List<PackageAndDiamond> getDiamondByStatus(String... status) {
		if (status == null || status.length == 0) {
			return Collections.emptyList();
		}
		List<PackageAndDiamond> list = Lists.newArrayList();
		for (String s : status) {
			list.addAll(diamondCache.getValuesByColumn(s));
		}
		return list;
	}

	private static void check(@Nonnull PackageAndDiamond pad) throws DiamondWebException {
		List<DiamondInfoData> orginalList = pad.getDiamondList();
		PackageInfo stat = pad.getPkgInfo();
		int dlSize = orginalList.size();
		int basketSize = stat.getDiamondsnumber();
		if (dlSize > basketSize) {
			throw new DiamondWebException("diamond in package is over the limit.");
		}

		BigDecimal total = BigDecimal.ZERO;
		final BigDecimal minWeight = stat.getMimweight();
		BigDecimal totalWeight = stat.getTotalweight();

		for (DiamondInfoData d : orginalList) {
			BigDecimal size = d.getSize();
			String giano = d.getGiano();
			if (size.compareTo(minWeight) < 0) {
				throw new DiamondWebException("diamond " + giano + " weight: " + size.toPlainString()
						+ " is less than minweight:" + minWeight.toPlainString());
			}

			total = total.add(size);
			if (total.compareTo(totalWeight) > 0) {
				throw new DiamondWebException("package " + stat.getBasketno() + " total weight:" + total.toPlainString()
						+ " is great than totalweight:" + totalWeight.toPlainString());
			}
		}

		if (dlSize == basketSize) {
			if (total.compareTo(totalWeight) < 0) {
				throw new DiamondWebException("package " + stat.getBasketno() + " total weight:" + total.toPlainString()
						+ " is less than totalweight:" + totalWeight.toPlainString());
			}
		}
	}

	/**
	 * 更改giano 时，更新tradeid 和giano映射与giano缓存
	 * 
	 * @param tradeid
	 * @param giano
	 * @param status
	 * @param basketno
	 */
	private void updateTradeIdGiaMap(String tradeid, String giano) {
		logger.debug("updateTradeIdGiaMap tradeId: {} ,giano: {}", tradeid, giano);
		tradeIdGiaMap.put(tradeid, giano);

	}

	/**
	 * 更改giano 时更新 package 中钻石信息
	 * 
	 * @param status
	 * @param basketno
	 * @param giano
	 */
	private void updateDiamondInPackage(String status, String basketno, String giano) {
		logger.debug("updateDiamondInPackage giano: {}, status:{},basketno: {}", giano, status, basketno);

		if (giaBasketnoMap.containsKey(giano)) {
			String bskno = giaBasketnoMap.get(giano);
			PackageAndDiamond opad = this.getDiamond(bskno, status);
			List<DiamondInfoData> ol = opad.getDiamondList();
			List<DiamondInfoData> tmp = ol.stream().filter(d -> !giano.equals(d.getGiano()))
					.collect(Collectors.toList());
			opad.setDiamondList(tmp);
		}
		giaBasketnoMap.put(giano, basketno);
	}

	public void setGiaCache(Set<String> giaList) {
		diamondSet = giaList;
	}

	public boolean containsDiamond(String giano) {
		synchronized (diamondSet) {
			return diamondSet.contains(giano);
		}
	}
/**
 * 
 * @param tradeid
 * @param giano
 * @return true: giano 改变，false giano不变
 * @throws DiamondWebException
 */
	public boolean checkGianoChange(String tradeid, String giano) throws DiamondWebException {
		String oldGia = tradeIdGiaMap.get(tradeid);
		if (oldGia == null) {
			throw new DiamondWebException("giano should not be null.");
		}
		logger.debug("checkGianoChange tradeid: {} ,oldGia: {} ,newGia: {}",tradeid,oldGia,giano);
		return !oldGia.equals(giano);
	}

	public boolean containsStatus(String status) {
		return diamondCache.containColumn(status);
	}

	public boolean containsPackage(String basketno) {
		return diamondCache.containRow(basketno);
	}

	public void remove(String basketno, String status) {
		PackageAndDiamond pad = diamondCache.remove(basketno, status);
		logger.info("remove diamond basketno: {} ,status:{}", basketno, status);
		List<DiamondInfoData> diamondList = pad.getDiamondList();
		if (diamondList == null) {
			logger.warn("package {} has no diamond,won't remove diamond.", basketno);
			return;
		}
		synchronized (diamondSet) {
			for (DiamondInfoData d : diamondList) {
				diamondSet.remove(d.getGiano());
				logger.debug("remove diamond basketno: {},giano: {}", basketno, d.getGiano());
			}
		}
	}
}
