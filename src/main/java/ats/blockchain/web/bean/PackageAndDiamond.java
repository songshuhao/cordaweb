package ats.blockchain.web.bean;

import java.util.List;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo1;
import ats.blockchain.web.model.Diamondsinfo;

public class PackageAndDiamond {

	private PackageInfo pkgInfo;

	private List<Diamondsinfo> diamondList;

	public PackageInfo getPkgInfo() {
		return pkgInfo;
	}

	public void setPkgInfo(PackageInfo pkgInfo) {
		this.pkgInfo = pkgInfo;
	}

	public List<Diamondsinfo> getDiamondList() {
		return diamondList;
	}

	public void setDiamondList(List<Diamondsinfo> diamondList) {
		this.diamondList = diamondList;
	}

}
