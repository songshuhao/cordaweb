package ats.blockchain.web.servcie.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ats.blockchain.web.dao.BasketinfoMapper;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.BasketinfoExample;
import ats.blockchain.web.servcie.BasketInfoServcie;

@Service
public class BasketInfoServcieImpl implements BasketInfoServcie
{

	@Autowired
	private BasketinfoMapper basketinfoMapper;
	@Override
	@Transactional
	public void addBasketInfo(Basketinfo basketinfo)
	{
		basketinfoMapper.insert(basketinfo);
	}
	@Override
	public List<Basketinfo> getBasketList(BasketinfoExample example)
	{
		List<Basketinfo> list = basketinfoMapper.selectByExample(example);
		if(null != list && list.size() > 0)
		{
			return list;
		}
		return new ArrayList<Basketinfo>();
	}
	@Override
	@Transactional
	public void updateBasketInfo(Basketinfo basketinfo)
	{
		basketinfoMapper.updateByPrimaryKeySelective(basketinfo);
	}

}
