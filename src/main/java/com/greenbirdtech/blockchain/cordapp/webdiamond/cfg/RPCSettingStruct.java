package com.greenbirdtech.blockchain.cordapp.webdiamond.cfg;

import java.io.Serializable;

public class RPCSettingStruct implements Serializable 
{
	private static final long serialVersionUID = -643533394549792256L;

	private boolean useSsl;
	private boolean standAloneBroker;
	private String address;
	private String adminAddress;
	
	public boolean isUseSsl() {
		return useSsl;
	}
	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}
	public boolean isStandAloneBroker() {
		return standAloneBroker;
	}
	public void setStandAloneBroker(boolean standAloneBroker) {
		this.standAloneBroker = standAloneBroker;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAdminAddress() {
		return adminAddress;
	}
	public void setAdminAddress(String adminAddress) {
		this.adminAddress = adminAddress;
	}
}
