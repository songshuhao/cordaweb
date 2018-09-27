package ats.blockchain.web.servcie;

import java.util.List;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageInfo;

public interface PackageInfoService {

	boolean addPackageInfo(PackageInfo pkgInf);

	List<PackageInfo> getPackageInfo();

	List<PackageInfo> getPackageInfoById(String... basketNo);

	List<PackageInfo> getPackageInfoByStatus(String... status);

	List<PackageState> getPackageStateByStatus(String... status);

	List<PackageState> getPackageStateById(String... basketNo);

	/**
	 * 
	 * @param pkgCreate
	 * @return issue失败的package列表
	 */
	List<PackageInfo> submitPackageByStatus(String status);
	
	
	/**
	 * aoc add lab
	 * gia add packageInfo
	 * @param packageInfo
	 * @return
	 */
	boolean labConfirmPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc add vault
	 * vault add packageinfo
	 * @param pkgInf
	 * @return
	 */
	boolean vaultAddPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc to gia
	 * gia to aoc
	 * aoc to vault
	 * vault to aoc 
	 * submit方法
	 * @param step
	 * @return
	 */
	List<PackageInfo> submitPackageInfo(String step);
	
	/**
	 * aoc change owner
	 * @param pkgInf
	 * @return
	 */
	boolean transferPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc to audit
	 * @param pkgInf
	 * @return
	 */
	boolean auditPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc to owner
	 * aoc to audit
	 * @param packageInfos
	 * @return
	 */
	List<PackageInfo> submitPackageInfo(List<PackageInfo> packageInfos,String step);
	
}
