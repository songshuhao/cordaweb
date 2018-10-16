package ats.blockchain.web.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import ats.blockchain.cordapp.diamond.util.Constants;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.utils.StringUtil;

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

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Logger logger  = LoggerFactory.getLogger(getClass());
	public void add(PackageInfo pkg) {
		logger.debug("add cache: {}",pkg);
		String seqNo = pkg.getSeqNo();
		if (StringUtils.isBlank(seqNo)) {
			seqNo = StringUtil.getPackageSeqno();
			pkg.setSeqNo(seqNo);
		}
		String status = pkg.getStatus();
		pkg.setStatusDesc(Constants.PKG_STATE_MAP.get(status));
		pkgCache.put(seqNo, pkg);
		synchronized (packageSet) {
			String basketno = pkg.getBasketno();
			if(seqNoBskNoMap.containsKey(seqNo)) {
				String oldBskNo = seqNoBskNoMap.get(seqNo);
				if (!oldBskNo.equals(basketno)) {
					seqNoBskNoMap.put(seqNo, basketno);
					packageSet.remove(oldBskNo);
					packageSet.add(basketno);
				}
			}else {
				seqNoBskNoMap.put(seqNo, basketno);
				packageSet.add(basketno);
			}
		}

	}

	public void setBasketnoCache(Set<String> bskList) {
		packageSet = bskList;
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

	public void addPackage(String basketno) {
		synchronized (packageSet) {
			packageSet.add(basketno);
		}
	}

	public PackageInfo getPackage(String seqNo, String status) {
		PackageInfo p = pkgCache.get(seqNo);
		return  status.equals(p.getStatus())?p:null;
		// return diamondCache.getValue(status, seqNo);
	}

	public List<PackageInfo> getPackageByStatus(String... status) {
		Set<String> set = Sets.newHashSet(status);
		List<PackageInfo> list = pkgCache.values().stream().filter(p -> set.contains(p.getStatus()))
				.collect(Collectors.toList());

		return list;
	}

	public PackageInfo remove(String seqNo, String status) {
		return null;
//		PackageInfo p = pkgCache.get(seqNo);
//		return status.equals(p.getStatus())?p:null;
//		return diamondCache.remove(status, seqNo);
	}
}
