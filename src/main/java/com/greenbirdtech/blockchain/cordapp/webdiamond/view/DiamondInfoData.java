package com.greenbirdtech.blockchain.cordapp.webdiamond.view;

import java.io.Serializable;

public class DiamondInfoData implements Serializable 
{
	private static final long serialVersionUID = 2314287951102401873L;

	private String giacertid;
	private String giacertdate;
	private String giacertinfo;
	private String miner;
	private String minedate;
	private String cutter;
	private String craftsmanname;
	private String craftsmandate;
	private String dealername;
	private String dealdate;
	
	public String getGiacertid() {
		return giacertid;
	}
	public void setGiacertid(String giacertid) {
		this.giacertid = giacertid;
	}
	public String getGiacertdate() {
		return giacertdate;
	}
	public void setGiacertdate(String giacertdate) {
		this.giacertdate = giacertdate;
	}
	public String getGiacertinfo() {
		return giacertinfo;
	}
	public void setGiacertinfo(String giacertinfo) {
		this.giacertinfo = giacertinfo;
	}
	public String getMiner() {
		return miner;
	}
	public void setMiner(String miner) {
		this.miner = miner;
	}
	public String getMinedate() {
		return minedate;
	}
	public void setMinedate(String minedate) {
		this.minedate = minedate;
	}
	public String getCutter() {
		return cutter;
	}
	public void setCutter(String cutter) {
		this.cutter = cutter;
	}
	public String getCraftsmanname() {
		return craftsmanname;
	}
	public void setCraftsmanname(String craftsmanname) {
		this.craftsmanname = craftsmanname;
	}
	public String getCraftsmandate() {
		return craftsmandate;
	}
	public void setCraftsmandate(String craftsmandate) {
		this.craftsmandate = craftsmandate;
	}
	public String getDealername() {
		return dealername;
	}
	public void setDealername(String dealername) {
		this.dealername = dealername;
	}
	public String getDealdate() {
		return dealdate;
	}
	public void setDealdate(String dealdate) {
		this.dealdate = dealdate;
	}
}
