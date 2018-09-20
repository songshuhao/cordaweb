package ats.blockchain.web.servcie;

import java.util.List;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageInfo;

public interface PackageInfoService {

	boolean addPackageInfo(PackageInfo pkgInf);

	List<PackageInfo> getPackageInfo();

	List<PackageInfo> getPackageInfoById(String basketNo);

	List<PackageInfo> getPackageInfoByStatus(String status);

	List<PackageState> getPackageStateByStatus(String status);

	List<PackageState> getPackageStateById(String basketNo);

	/**
	 * 
	 * @param pkgCreate
	 * @return issue失败的package列表
	 */
	List<PackageInfo> submitPackageByStatus(String pkgCreate);
}
