package ats.blockchain.web.model;

public class Product
{
	private String productcode;
	private String color;
	private String clarity;
	private String shape;
	private String cut;
	private String polish;
	private String symmetry;
	
	public String getProductcode()
	{
		return productcode;
	}
	public void setProductcode(String productcode)
	{
		this.productcode = productcode;
	}
	public String getColor()
	{
		return color;
	}
	public void setColor(String color)
	{
		this.color = color;
	}
	public String getClarity()
	{
		return clarity;
	}
	public void setClarity(String clarity)
	{
		this.clarity = clarity;
	}
	public String getShape()
	{
		return shape;
	}
	public void setShape(String shape)
	{
		this.shape = shape;
	}
	public String getCut()
	{
		return cut;
	}
	public void setCut(String cut)
	{
		this.cut = cut;
	}
	public String getPolish()
	{
		return polish;
	}
	public void setPolish(String polish)
	{
		this.polish = polish;
	}
	public String getSymmetry()
	{
		return symmetry;
	}
	public void setSymmetry(String symmetry)
	{
		this.symmetry = symmetry;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Product [productcode=");
		builder.append(productcode);
		builder.append(", color=");
		builder.append(color);
		builder.append(", clarity=");
		builder.append(clarity);
		builder.append(", shape=");
		builder.append(shape);
		builder.append(", cut=");
		builder.append(cut);
		builder.append(", polish=");
		builder.append(polish);
		builder.append(", symmetry=");
		builder.append(symmetry);
		builder.append("]");
		return builder.toString();
	}
}
