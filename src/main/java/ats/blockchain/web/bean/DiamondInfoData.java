package ats.blockchain.web.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class DiamondInfoData  implements Serializable{
	private static final long serialVersionUID = 4122375686325300081L;
	private int tradeid;
	private String basketno;
	private String giano;
	private String reqcode;

	private String reqdate;

	private String resdate;

	private String suppliercode;

	private String suppliername;

	private String productcode;

	private String shape;

	private BigDecimal size;

	private String color;

	private String clarity;

	private String cut;

	private String polish;

	private String symmetry;

	private String status;
	private String statusDesc;

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	private String origin;

	private String minedate;

	private String cutter;

	private String craftsmanname;

	private String craftsmandate;

	private String dealername;

	private String dealerdate;

	private String remark1;

	private String remark2;

	private String remark3;

	private String remark4;

	private String remark5;

	public int getTradeid() {
		return tradeid;
	}

	public void setTradeid(int tradeid) {
		this.tradeid = tradeid;
	}

	public String getBasketno() {
		return basketno;
	}

	public void setBasketno(String basketno) {
		this.basketno = basketno;
	}

	public String getGiano() {
		return giano;
	}

	public void setGiano(String giano) {
		this.giano = giano;
	}

	public String getReqcode() {
		return reqcode;
	}

	public void setReqcode(String reqcode) {
		this.reqcode = reqcode;
	}

	public String getReqdate() {
		return reqdate;
	}

	public void setReqdate(String reqdate) {
		this.reqdate = reqdate;
	}

	public String getResdate() {
		return resdate;
	}

	public void setResdate(String resdate) {
		this.resdate = resdate;
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

	public String getProductcode() {
		return productcode;
	}

	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public BigDecimal getSize() {
		return size;
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getClarity() {
		return clarity;
	}

	public void setClarity(String clarity) {
		this.clarity = clarity;
	}

	public String getCut() {
		return cut;
	}

	public void setCut(String cut) {
		this.cut = cut;
	}

	public String getPolish() {
		return polish;
	}

	public void setPolish(String polish) {
		this.polish = polish;
	}

	public String getSymmetry() {
		return symmetry;
	}

	public void setSymmetry(String symmetry) {
		this.symmetry = symmetry;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
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

	public String getDealerdate() {
		return dealerdate;
	}

	public void setDealerdate(String dealerdate) {
		this.dealerdate = dealerdate;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getRemark3() {
		return remark3;
	}

	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}

	public String getRemark4() {
		return remark4;
	}

	public void setRemark4(String remark4) {
		this.remark4 = remark4;
	}

	public String getRemark5() {
		return remark5;
	}

	public void setRemark5(String remark5) {
		this.remark5 = remark5;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DiamondInfoData [tradeid=");
		builder.append(tradeid);
		builder.append(", basketno=");
		builder.append(basketno);
		builder.append(", giano=");
		builder.append(giano);
		builder.append(", reqcode=");
		builder.append(reqcode);
		builder.append(", reqdate=");
		builder.append(reqdate);
		builder.append(", resdate=");
		builder.append(resdate);
		builder.append(", suppliercode=");
		builder.append(suppliercode);
		builder.append(", suppliername=");
		builder.append(suppliername);
		builder.append(", productcode=");
		builder.append(productcode);
		builder.append(", shape=");
		builder.append(shape);
		builder.append(", size=");
		builder.append(size);
		builder.append(", color=");
		builder.append(color);
		builder.append(", clarity=");
		builder.append(clarity);
		builder.append(", cut=");
		builder.append(cut);
		builder.append(", polish=");
		builder.append(polish);
		builder.append(", symmetry=");
		builder.append(symmetry);
		builder.append(", status=");
		builder.append(status);
		builder.append(", statusDesc=");
		builder.append(statusDesc);
		builder.append(", origin=");
		builder.append(origin);
		builder.append(", minedate=");
		builder.append(minedate);
		builder.append(", cutter=");
		builder.append(cutter);
		builder.append(", craftsmanname=");
		builder.append(craftsmanname);
		builder.append(", craftsmandate=");
		builder.append(craftsmandate);
		builder.append(", dealername=");
		builder.append(dealername);
		builder.append(", dealerdate=");
		builder.append(dealerdate);
		builder.append(", remark1=");
		builder.append(remark1);
		builder.append(", remark2=");
		builder.append(remark2);
		builder.append(", remark3=");
		builder.append(remark3);
		builder.append(", remark4=");
		builder.append(remark4);
		builder.append(", remark5=");
		builder.append(remark5);
		builder.append("]");
		return builder.toString();
	}
}