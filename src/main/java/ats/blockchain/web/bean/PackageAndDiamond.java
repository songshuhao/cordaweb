package ats.blockchain.web.bean;

import java.util.List;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo1;

public class PackageAndDiamond {

	private PackageInfo pkgInfo;

	private List<DiamondsInfo1> diamondList;

	public PackageInfo getPkgInfo() {
		return pkgInfo;
	}

	public void setPkgInfo(PackageInfo pkgInfo) {
		this.pkgInfo = pkgInfo;
	}

	public List<DiamondsInfo1> getDiamondList() {
		return diamondList;
	}

	public void setDiamondList(List<DiamondsInfo1> diamondList) {
		this.diamondList = diamondList;
	}

}
