package ats.blockchain.web.model;

import java.util.List;

import ats.blockchain.web.bean.PackageInfo;

public class BasketViewObject
{
	private List<PackageInfo> packageInfos;
	
	private String step;
	
	public List<PackageInfo> getPackageInfos()
	{
		return packageInfos;
	}

	public void setPackageInfos(List<PackageInfo> packageInfos)
	{
		this.packageInfos = packageInfos;
	}

	public String getStep()
	{
		return step;
	}

	public void setStep(String step)
	{
		this.step = step;
	}
}
