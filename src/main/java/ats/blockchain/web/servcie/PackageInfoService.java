package ats.blockchain.web.servcie;

import java.util.List;
import java.util.Map;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;

public interface PackageInfoService {

	Map<String,Object> addPackageInfo(PackageInfo pkgInf);
	
	Map<String,Object> editPackageInfo(PackageInfo pkgInf);
	
	List<PackageInfo> getPackageInfo();

	List<PackageInfo> getPackageInfoById(String... basketNo);

	List<PackageInfo> getPackageInfoByStatus(String userid, String... status);

	List<PackageState> getPackageStateByStatus(String... status);

	List<PackageState> getPackageStateById(String... basketNo);
	
	List<PackageAndDiamond> getPackageAndDiamondById(String... basketNo);
	
	List<PackageInfo> getPackageStateWithoutRedeemByStatus(String redeemOwnerId,String... status);

	/**
	 * 
	 * @param userid 
	 * @param pkgCreate
	 * @return issue失败的package列表
	 */
	List<PackageInfo> submitPackageByStatus(String status, String userid);
	
	
	/**
	 * aoc add lab
	 * gia add packageInfo
	 * @author shuhao.song
	 * @param packageInfo
	 * @return
	 */
	boolean labConfirmPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc add vault
	 * vault add packageinfo
	 * @author shuhao.song
	 * @param pkgInf
	 * @return
	 */
	boolean vaultAddPackageInfo(PackageInfo packageInfo);
	
	/**
	 * step:<br>
	 * aoc to gia,
	 * gia to aoc,
	 * aoc to vault,
	 * vault to aoc ,
	 * submit方法
	 * @author shuhao.song
	 * @param step
	 * @param userid
	 * @return
	 */
	List<PackageInfo> submitPackageInfo(String step, String userid);
	/**
	 * aoc change owner
	 * @author shuhao.song
	 * @param pkgInf
	 * @return
	 */
	boolean transferPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc to audit
	 * @author shuhao.song
	 * @param pkgInf
	 * @return
	 */
	boolean auditPackageInfo(PackageInfo packageInfo);
	
	/**
	 * aoc to owner
	 * aoc to audit
	 * @author shuhao.song
	 * @param packageInfos
	 * @return
	 */
	List<PackageInfo> submitPackageInfo(List<PackageInfo> packageInfos,String step);

	
	
}
