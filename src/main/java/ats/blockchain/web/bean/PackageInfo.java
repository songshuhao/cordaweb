package ats.blockchain.web.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class PackageInfo implements Serializable {
	private static final long serialVersionUID = -3145094899251858899L;
	private String seqNo;
	private String userid;
	private String aoc;
	private String basketno;
	private String productcode;
	private String uuid;
	private String suppliercode;
	private String suppliername;
	private int diamondsnumber;
	private BigDecimal totalweight;
	private BigDecimal mimweight;
	private String giacontrolno;
	private String gradlab;
	private String locgradlab;
	private String collectionadd;
	private String invtymgr;
	private String sealedbagno;
	private String depaccno;
	private String reverification;
	private String ownmgr;
	private String auditor;
	private String vault;
	private String owner;
	private String location;
	private String auditdate;
	private String ownerdate;
	private String locdate;
	private String giaapproveddate;
	private String result;
	private String status;
	private String statusDesc;

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getAoc() {
		return aoc;
	}

	public void setAoc(String aoc) {
		this.aoc = aoc;
	}

	public String getBasketno() {
		return basketno;
	}

	public void setBasketno(String basketno) {
		this.basketno = basketno;
	}

	public String getProductcode() {
		return productcode;
	}

	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}

	public String getSuppliercode() {
		return suppliercode;
	}

	public void setSuppliercode(String suppliercode) {
		this.suppliercode = suppliercode;
	}

	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	public int getDiamondsnumber() {
		return diamondsnumber;
	}

	public void setDiamondsnumber(int diamondsnumber) {
		this.diamondsnumber = diamondsnumber;
	}

	public BigDecimal getTotalweight() {
		return totalweight;
	}

	public void setTotalweight(BigDecimal totalweight) {
		this.totalweight = totalweight;
	}

	public BigDecimal getMimweight() {
		return mimweight;
	}

	public void setMimweight(BigDecimal mimweight) {
		this.mimweight = mimweight;
	}

	public String getGiacontrolno() {
		return giacontrolno;
	}

	public void setGiacontrolno(String giacontrolno) {
		this.giacontrolno = giacontrolno;
	}

	public String getGradlab() {
		return gradlab;
	}

	public void setGradlab(String gradlab) {
		this.gradlab = gradlab;
	}

	public String getLocgradlab() {
		return locgradlab;
	}

	public void setLocgradlab(String locgradlab) {
		this.locgradlab = locgradlab;
	}

	public String getCollectionadd() {
		return collectionadd;
	}

	public void setCollectionadd(String collectionadd) {
		this.collectionadd = collectionadd;
	}

	public String getInvtymgr() {
		return invtymgr;
	}

	public void setInvtymgr(String invtymgr) {
		this.invtymgr = invtymgr;
	}

	public String getSealedbagno() {
		return sealedbagno;
	}

	public void setSealedbagno(String sealedbagno) {
		this.sealedbagno = sealedbagno;
	}

	public String getDepaccno() {
		return depaccno;
	}

	public void setDepaccno(String depaccno) {
		this.depaccno = depaccno;
	}

	public String getOwnmgr() {
		return ownmgr;
	}

	public void setOwnmgr(String ownmgr) {
		this.ownmgr = ownmgr;
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

	public String getGiaapproveddate() {
		return giaapproveddate;
	}

	public void setGiaapproveddate(String giaapproveddate) {
		this.giaapproveddate = giaapproveddate;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getReverification() {
		return reverification;
	}

	public void setReverification(String reverification) {
		this.reverification = reverification;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PackageInfo [seqNo=");
		builder.append(seqNo);
		builder.append(", userid=");
		builder.append(userid);
		builder.append(", aoc=");
		builder.append(aoc);
		builder.append(", basketno=");
		builder.append(basketno);
		builder.append(", productcode=");
		builder.append(productcode);
		builder.append(", uuid=");
		builder.append(uuid);
		builder.append(", suppliercode=");
		builder.append(suppliercode);
		builder.append(", suppliername=");
		builder.append(suppliername);
		builder.append(", diamondsnumber=");
		builder.append(diamondsnumber);
		builder.append(", totalweight=");
		builder.append(totalweight);
		builder.append(", mimweight=");
		builder.append(mimweight);
		builder.append(", giacontrolno=");
		builder.append(giacontrolno);
		builder.append(", gradlab=");
		builder.append(gradlab);
		builder.append(", locgradlab=");
		builder.append(locgradlab);
		builder.append(", collectionadd=");
		builder.append(collectionadd);
		builder.append(", invtymgr=");
		builder.append(invtymgr);
		builder.append(", sealedbagno=");
		builder.append(sealedbagno);
		builder.append(", depaccno=");
		builder.append(depaccno);
		builder.append(", reverification=");
		builder.append(reverification);
		builder.append(", ownmgr=");
		builder.append(ownmgr);
		builder.append(", auditor=");
		builder.append(auditor);
		builder.append(", vault=");
		builder.append(vault);
		builder.append(", owner=");
		builder.append(owner);
		builder.append(", location=");
		builder.append(location);
		builder.append(", auditdate=");
		builder.append(auditdate);
		builder.append(", ownerdate=");
		builder.append(ownerdate);
		builder.append(", locdate=");
		builder.append(locdate);
		builder.append(", giaapproveddate=");
		builder.append(giaapproveddate);
		builder.append(", result=");
		builder.append(result);
		builder.append(", status=");
		builder.append(status);
		builder.append(", statusDesc=");
		builder.append(statusDesc);
		builder.append("]");
		return builder.toString();
	}

}