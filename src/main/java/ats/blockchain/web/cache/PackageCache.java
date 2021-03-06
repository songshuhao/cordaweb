package ats.blockchain.web.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import ats.blockchain.cordapp.diamond.util.Constants;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.PackageInfo;

public class PackageCache {
	/**
	 * row :status<br>
	 * column: seqNo<br>
	 * value: PackageInfo
	 */
	// private TableCache<String, String, PackageInfo> diamondCache = new
	// TableCache<>();
	/**
	 * key: seqNo<br>
	 * value: PackageInfo
	 */
	private ConcurrentMap<String, PackageInfo> pkgCache = new ConcurrentHashMap<String, PackageInfo>();

	/**
	 * 缓存所有basketno 用于校验篮子是否重复
	 */
	private Set<String> packageSet;
	/**
	 * 缓存seqno 和basketno 的对应关系
	 */
	private Map<String, String> seqNoBskNoMap = new HashMap<String, String>();

//	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void add(PackageInfo pkg) {
		logger.debug("add package into cache: {}",pkg);
		String seqNo = pkg.getSeqNo();
		String status = pkg.getStatus();
		pkgCache.put(seqNo, pkg);
		pkg.setStatusDesc(Constants.PKG_STATE_MAP.get(status));
		synchronized (packageSet) {
			String basketno = pkg.getBasketno();
			if (seqNoBskNoMap.containsKey(seqNo)) {
				String oldBskNo = seqNoBskNoMap.get(seqNo);
				if (!oldBskNo.equals(basketno)) {
					seqNoBskNoMap.put(seqNo, basketno);
					packageSet.remove(oldBskNo);
					packageSet.add(basketno);
				}
			} else {
				seqNoBskNoMap.put(seqNo, basketno);
				packageSet.add(basketno);
			}
		}
	}
	
	public void update(PackageInfo pkg) {
		String seqNo = pkg.getSeqNo();
		if(pkgCache.containsKey(seqNo)) {
			logger.debug("package in cache ,don't add again,baksetno: {},status : {}",pkg.getBasketno(),pkg.getStatusDesc());
			return;
		}
		logger.debug("update package cache :{}",pkg);
		add(pkg);
	}
	
	public void setBasketnoCache(Set<String> bskList) {
		packageSet = bskList;
	}

	public PackageInfo getPackage(String seqNo) {
		return pkgCache.get(seqNo);
	}

	/**
	 * 检查篮子编号是否存在
	 * 
	 * @param basketno
	 * @return
	 */
	public boolean containsPackage(String basketno) {
		synchronized (packageSet) {
			return packageSet.contains(basketno);
		}
	}
	/**
	 * 该seqno对应的basketno 是否改变
	 * @param seqno
	 * @param basketno
	 * @return true: basketno 改变，false basketno不变
	 * @throws DiamondWebException
	 */
	public boolean checkPackagenoChange(String seqno,String basketno) {
		if(seqno==null) {
			return false;
		}
		if(pkgCache.containsKey(seqno)) {
			String old = pkgCache.get(seqno).getBasketno();
			return !old.equals(basketno);
		}
		return true;
	}

	public PackageInfo getPackage(String seqNo, String status) {
		PackageInfo p = pkgCache.get(seqNo);
		return status.equals(p.getStatus()) ? p : null;
		// return diamondCache.getValue(status, seqNo);
	}

	public List<PackageInfo> getPackageByStatus(String... status) {
		Set<String> set = Sets.newHashSet(status);
		List<PackageInfo> list = pkgCache.values().stream().filter(p -> set.contains(p.getStatus()))
				.collect(Collectors.toList());
		logger.debug("get package by status {},list size: {} ,content: {}",set,list.size(),list);
		return list;
	}
	/**
	 *  界面点击删除，需要同步删除packageSet，seqNoBskNoMap
	 * @param seqNo
	 * @param status
	 */
	public void removePackage(String seqNo, String status) {
		remove(seqNo, status);
		synchronized (packageSet) {
			if(seqNoBskNoMap.containsKey(seqNo)) {
				String basketno = seqNoBskNoMap.remove(seqNo);
				packageSet.remove(basketno);
			}
		}
	}

	/**
	 * 提交corda成功后删除
	 * @param seqNo
	 * @param status
	 * @return
	 */
	public PackageInfo remove(String seqNo, String status) {
		logger.debug("remove seqNo: {} ,status: {} from package cache.",seqNo,status);
		PackageInfo p = pkgCache.get(seqNo);
		boolean equals = status.equals(p.getStatus());
		return equals ? pkgCache.remove(seqNo) : null;
	}
}
