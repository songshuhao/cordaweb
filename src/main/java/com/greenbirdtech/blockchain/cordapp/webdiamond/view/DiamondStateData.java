package com.greenbirdtech.blockchain.cordapp.webdiamond.view;

import java.io.Serializable;

public class DiamondStateData implements Serializable 
{
	public static final String[] STRSTATE = {"Supplied","Owner Change Request","Owner Change","Vault Change Request",
		"Vault Change","Auditor Request","Auditor Confirmed","Redeemed"
	};
	
	private static final long serialVersionUID = -8295900652906494335L;

	private String externalid;
	private String uuid;
	private String aoc;
	private String supplier;
	private String ownmgr;
	private String auditor;
	private String vault;
	private String owner;
	private String initprice;
	private String txnprice;
	private String location;
	private String auditdate;
	private String ownerdate;
	private String locdate;
	private int status;
	private String statusstr;
	
	public String getExternalid() {
		return externalid;
	}
	public void setExternalid(String externalid) {
		this.externalid = externalid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getAoc() {
		return aoc;
	}
	public void setAoc(String aoc) {
		this.aoc = aoc;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	public String getVault() {
		return vault;
	}
	public void setVault(String vault) {
		this.vault = vault;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getInitprice() {
		return initprice;
	}
	public void setInitprice(String initprice) {
		this.initprice = initprice;
	}
	public String getTxnprice() {
		return txnprice;
	}
	public void setTxnprice(String txnprice) {
		this.txnprice = txnprice;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAuditdate() {
		return auditdate;
	}
	public void setAuditdate(String auditdate) {
		this.auditdate = auditdate;
	}
	public String getOwnmgr() {
		return ownmgr;
	}
	public void setOwnmgr(String ownmgr) {
		this.ownmgr = ownmgr;
	}
	public String getOwnerdate() {
		return ownerdate;
	}
	public void setOwnerdate(String ownerdate) {
		this.ownerdate = ownerdate;
	}
	public String getLocdate() {
		return locdate;
	}
	public void setLocdate(String locdate) {
		this.locdate = locdate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusstr() {
		return statusstr;
	}
	public void setStatusstr(String statusstr) {
		this.statusstr = statusstr;
	}
}
