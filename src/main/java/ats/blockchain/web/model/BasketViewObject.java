package ats.blockchain.web.model;

import java.util.List;

public class BasketViewObject
{
	private List<Basketinfo> basketinfos;
	
	private String step;

	public List<Basketinfo> getBasketinfos()
	{
		return basketinfos;
	}

	public void setBasketinfos(List<Basketinfo> basketinfos)
	{
		this.basketinfos = basketinfos;
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
