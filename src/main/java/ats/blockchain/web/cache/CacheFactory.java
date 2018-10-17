package ats.blockchain.web.cache;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageAndDiamond;

/**
 * 获取不同用户的缓存
 * 
 * @author shi hongyu
 *
 */
public enum CacheFactory {
	Instance;

	private ConcurrentMap<String, PackageCache> userPackageCache = Maps.newConcurrentMap();
	private ConcurrentMap<String, DiamondCache> userDiamondCache = Maps.newConcurrentMap();

	private Set<String> gianoSet = null;
	private Set<String> packageSet = null;
	private List<PackageAndDiamond> padList;

	public void init(Set<String> gianoSet, Set<String> pkgSet, List<PackageAndDiamond> padList) {
		this.gianoSet = gianoSet;
		packageSet = pkgSet;
		this.padList = padList;
	}

	/**
	 * 获取用户的package信息缓存
	 * 
	 * @param userId
	 * @return
	 */
	public PackageCache getPackageCache(String userId) {
		PackageCache cache = null;
		if (userPackageCache.containsKey(userId)) {
			cache = userPackageCache.get(userId);
		} else {
			cache = new PackageCache();
			cache.setBasketnoCache(packageSet);
			if (padList != null) {
				for (PackageAndDiamond p : padList) {
					cache.add(p.getPkgInfo());
				}
			}
			PackageCache old = userPackageCache.putIfAbsent(userId, cache);
			cache = old == null ? cache : old;
		}
		return cache;
	}

	/**
	 * 获取用户的钻石信息缓存
	 * 
	 * @param userId
	 * @return
	 */
	public DiamondCache getDiamondCache(String userId) {
		DiamondCache cache = null;
		if (userDiamondCache.containsKey(userId)) {
			cache = userDiamondCache.get(userId);
		} else {
			cache = new DiamondCache();
			cache.setGiaCache(gianoSet);
			if (padList != null) {
				for (PackageAndDiamond p : padList) {
					if(PackageState.PKG_ISSUE.equals(p.getPkgInfo().getStatus())) {
						cache.add(p);
					}
				}
			}
			DiamondCache old = userDiamondCache.putIfAbsent(userId, cache);
			cache = old == null ? cache : old;
		}
		return cache;
	}
}
