package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import ats.blockchain.web.dao.DiamondsinfoMapper;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.BasketinfoExample;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.model.DiamondsinfoExample;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.BasketInfoServcie;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.DateFormatUtils;

@Controller
public class ConfimDiamondsInfoController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource(name="basketInfoServcieImpl")
	private BasketInfoServcie basketInfoServcie;
	@Autowired
	private DiamondsinfoMapper diamondsinfoMapper;
	
	@RequestMapping("/confirm/confirmList")
	public String findBasketList()
	{
		return "confirmList";
	}
	
	@RequestMapping("/confirm/confirmgiaList")
	public String findConfirmgiaList()
	{
		return "confirmgiaList";
	}
	
	
	@RequestMapping("/confirm/updateBasketInfo")
	@ResponseBody
	public String updateBasketInfo(Basketinfo basketinfo,String step) throws JSONException
	{
		logger.debug("BasketInfoController:basketinfo---->" + basketinfo.toString());
		
		JSONObject result = new JSONObject();
		String status = "";
		try
		{
			if(StringUtils.isNotBlank(step))
			{
				if(step.equals("atg"))
				{
					status = Constants.status_basket_confirm_into;
				}else if(step.equals("gta"))
				{
					status = Constants.status_basket_gia_into;
				}
			}
			basketinfo.setStatus(status);
			basketinfo.setGiaapproveddate(DateFormatUtils.format(basketinfo.getGiaapproveddate(), "-"));
			basketInfoServcie.updateBasketInfo(basketinfo);
			result.put("state", "success");
		} catch (Exception e)
		{
			logger.error("BasketInfoController--->updateBasketInfo：" + e.getMessage());
			result.put("state", "fail");
			result.put("message",e.getMessage().toString());
			e.printStackTrace();
		}
		return result.toString();

	}
	
	//@RequestMapping("/getBasketList")后台分页
	//@ResponseBody
	public PagedObjectDTO getBasketListServer(@RequestParam int pageNumber,int pageSize,HttpServletRequest request)
	{
		BasketinfoExample example = new BasketinfoExample();
		PageHelper.startPage(pageNumber,pageSize);
		List<Basketinfo> list =basketInfoServcie.getBasketList(example);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		PageInfo<Basketinfo> pageInfo = new PageInfo<>(list);
		if (list != null && !list.isEmpty()) {
			pagedObjectDTO.setRows(list);
			pagedObjectDTO.setTotal(Long.valueOf(pageInfo.getTotal()));
			return pagedObjectDTO;
		}
		return null;
		
	}
	
	@RequestMapping("/confirm/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber,int pageSize,String step,HttpServletRequest request) throws JSONException
	{
		BasketinfoExample example = new BasketinfoExample();
		List<String> statusList = new ArrayList<String>();

		if(StringUtils.isNotBlank(step))
		{
			if(step.equals("atg"))
			{
				statusList.add(Constants.status_diamonds_sumbmit);
				statusList.add(Constants.status_basket_confirm_into);
			}else if(step.equals("gta"))
			{
				statusList.add(Constants.status_basket_gia_into);
				statusList.add(Constants.status_basket_confirm_submit);
			}
		}
		
		example.createCriteria().andStatusIn(statusList);
		List<Basketinfo> list =basketInfoServcie.getBasketList(example);
		PagedObjectDTO result = new PagedObjectDTO();
		result.setRows(list = (list == null ? new ArrayList<Basketinfo>() : list));
		result.setTotal(Long.valueOf(list.size()));
		return result;
		
	}
	
	@RequestMapping("/confirm/submitBasketList")
	@ResponseBody
	public String submitBasketList(HttpServletRequest request,String step) throws JSONException
	{
		//1，查询需要提交的basket信息
		BasketinfoExample example = new BasketinfoExample();
		JSONObject result = new JSONObject();
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals("atg"))
			{
				example.createCriteria().andStatusEqualTo(Constants.status_diamonds_sumbmit);
				List<Basketinfo> list =basketInfoServcie.getBasketList(example);
				
				if(null != list && list.size() >0)
				{
					String message = "these bakset shoud be confirmed all[";
					for(int i=0; i<list.size(); i++)
					{
						if(message.indexOf(list.get(i).getBasketno())==-1)
						{
							message = message+list.get(i).getBasketno()+":";
						}
						
					}
					message = message+"]";
					result.put("state", "fail");
					result.put("message",message);
				}else
				{
					example.clear();
					example.createCriteria().andStatusEqualTo(Constants.status_basket_confirm_into);
					list =basketInfoServcie.getBasketList(example);
					try
					{
						if(null != list && list.size() >0)
						{
							//2，调用cordaAPI进行区块链存储
							
							//3，存储成功之后更新basket信息
							Diamondsinfo diamondsinfo = new Diamondsinfo();
							DiamondsinfoExample diamondsinfoExample = new DiamondsinfoExample();
							for(Basketinfo basketinfo : list)
							{
								basketinfo.setStatus(Constants.status_basket_confirm_submit);
								basketInfoServcie.updateBasketInfo(basketinfo);
								
								//更新钻石状态
								diamondsinfo.setBasketno(basketinfo.getBasketno());
								diamondsinfo.setStatus(basketinfo.getStatus());
								diamondsinfoExample.createCriteria().andBasketnoEqualTo(basketinfo.getBasketno());
								diamondsinfoMapper.updateByExampleSelective(diamondsinfo, diamondsinfoExample);
							}
							result.put("state", "success");
						}else
						{
							result.put("state", "fail");
							result.put("message", "There is not data to submit");
						}
						
					} catch (Exception e)
					{
						logger.error("DiamondsInfoController:error：" + e.getMessage());
						result.put("state", "fail");
						result.put("message",e.getMessage().toString());
						e.printStackTrace();
					}
				}
			}else if(step.equals("gta"))
			{

				example.createCriteria().andStatusEqualTo(Constants.status_basket_confirm_submit);
				List<Basketinfo> list =basketInfoServcie.getBasketList(example);
				
				if(null != list && list.size() >0)
				{
					String message = "these bakset shoud be added all[";
					for(int i=0; i<list.size(); i++)
					{
						if(message.indexOf(list.get(i).getBasketno())==-1)
						{
							message = message+list.get(i).getBasketno()+":";
						}
						
					}
					message = message+"]";
					result.put("state", "fail");
					result.put("message",message);
				}else
				{
					example.clear();
					example.createCriteria().andStatusEqualTo(Constants.status_basket_gia_into);
					list =basketInfoServcie.getBasketList(example);
					try
					{
						if(null != list && list.size() >0)
						{
							//2，调用cordaAPI进行区块链存储
							
							//3，存储成功之后更新basket信息
							Diamondsinfo diamondsinfo = new Diamondsinfo();
							DiamondsinfoExample diamondsinfoExample = new DiamondsinfoExample();
							for(Basketinfo basketinfo : list)
							{
								basketinfo.setStatus(Constants.status_basket_gia_submit);
								basketInfoServcie.updateBasketInfo(basketinfo);
								
								//更新钻石状态
								diamondsinfo.setBasketno(basketinfo.getBasketno());
								diamondsinfo.setStatus(basketinfo.getStatus());
								diamondsinfoExample.createCriteria().andBasketnoEqualTo(basketinfo.getBasketno());
								diamondsinfoMapper.updateByExampleSelective(diamondsinfo, diamondsinfoExample);
							}
							result.put("state", "success");
						}else
						{
							result.put("state", "fail");
							result.put("message", "There is not data to submit");
						}
						
					} catch (Exception e)
					{
						logger.error("DiamondsInfoController:error：" + e.getMessage());
						result.put("state", "fail");
						result.put("message",e.getMessage().toString());
						e.printStackTrace();
					}
				}
			
			}else
			{
				result.put("state", "fail");
				result.put("message","There is no data to submit");
			}
			
		}else
		{
			result.put("state", "fail");
			result.put("message","There is no data to submit");
		}
		return result.toString();
	}
}
