package ats.blockchain.web.bean;

import java.util.List;

public class PackageAndDiamond {

	private PackageInfo pkgInfo;

	private List<DiamondInfoData> diamondList;

	public PackageInfo getPkgInfo() {
		return pkgInfo;
	}

	public void setPkgInfo(PackageInfo pkgInfo) {
		this.pkgInfo = pkgInfo;
	}

	public List<DiamondInfoData> getDiamondList() {
		return diamondList;
	}

	public void setDiamondList(List<DiamondInfoData> diamondList) {
		this.diamondList = diamondList;
	}

}
