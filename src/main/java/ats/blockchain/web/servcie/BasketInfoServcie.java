package ats.blockchain.web.servcie;

import java.util.List;

import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.BasketinfoExample;

public interface BasketInfoServcie
{
	void addBasketInfo(Basketinfo basketinfo);
	
	List<Basketinfo> getBasketList(BasketinfoExample example);
	
	void updateBasketInfo(Basketinfo basketinfo);
}
