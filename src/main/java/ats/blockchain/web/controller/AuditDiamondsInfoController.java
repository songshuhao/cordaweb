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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import ats.blockchain.web.dao.BasketinfoMapper;
import ats.blockchain.web.dao.DiamondsinfoMapper;
import ats.blockchain.web.model.BasketViewObject;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.BasketinfoExample;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.model.DiamondsinfoExample;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.BasketInfoServcie;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.DateFormatUtils;

@Controller
public class AuditDiamondsInfoController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource(name="basketInfoServcieImpl")
	private BasketInfoServcie basketInfoServcie;
	@Autowired
	private BasketinfoMapper basketinfoMapper;
	@Autowired
	private DiamondsinfoMapper diamondsinfoMapper;
	
	@RequestMapping("/audit/auditList")
	public String findBasketList()
	{
		return "auditList";
	}
	
	@RequestMapping("/audit/auditauditerList")
	public String findConfirmgiaList()
	{
		return "auditauditerList";
	}
	
	
	@RequestMapping("/audit/updateBasketInfo")
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
				if(step.equals("atau"))
				{
					//status = Constants.status;
				}else if(step.equals("auta"))
				{
					status = Constants.status_basket_audit_auditer_into;
				}
			}
			basketinfo.setStatus(status);
			basketinfo.setAuditdate((DateFormatUtils.format(basketinfo.getAuditdate(), "-")));
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
	
	@RequestMapping("/audit/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber,int pageSize,String step,HttpServletRequest request) throws JSONException
	{
		BasketinfoExample example = new BasketinfoExample();
		List<String> statusList = new ArrayList<String>();

		if(StringUtils.isNotBlank(step))
		{
			if(step.equals("atau"))
			{
				statusList.add(Constants.status_basket_move_valut_submit);
				statusList.add(Constants.status_basket_transfer_vault_submit);
			}else if(step.equals("auta"))
			{
				statusList.add(Constants.status_basket_audit_submit);
				statusList.add(Constants.status_basket_audit_auditer_into);
			}
		}
		
		example.createCriteria().andStatusIn(statusList);
		List<Basketinfo> list =basketInfoServcie.getBasketList(example);
		PagedObjectDTO result = new PagedObjectDTO();
		result.setRows(list = (list == null ? new ArrayList<Basketinfo>() : list));
		result.setTotal(Long.valueOf(list.size()));
		return result;
		
	}
	
	@RequestMapping(value="/audit/submitBasketList")
	@ResponseBody
	public String submitBasketList(@RequestBody BasketViewObject basketViewObject) throws JSONException
	{
		//1，查询需要提交的basket信息
		JSONObject result = new JSONObject();
		String step = basketViewObject.getStep() == null ? "" : basketViewObject.getStep();
		List<Basketinfo> baseinfoList = basketViewObject.getBasketinfos();
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals("atau"))
			{
				if(null != baseinfoList && baseinfoList.size() >0)
				{
					//2，调用cordaAPI进行区块链存储
					
					//3，存储成功之后更新basket信息
					Diamondsinfo diamondsinfo = new Diamondsinfo();
					DiamondsinfoExample diamondsinfoExample = new DiamondsinfoExample();
					for(Basketinfo basketinfo : baseinfoList)
					{
						basketinfo.setStatus(Constants.status_basket_audit_submit);
						basketInfoServcie.updateBasketInfo(basketinfo);
						//更新钻石状态
						diamondsinfo.setBasketno(basketinfo.getBasketno());
						diamondsinfo.setStatus(basketinfo.getStatus());
						diamondsinfoExample.createCriteria().andBasketnoEqualTo(basketinfo.getBasketno());
						diamondsinfoMapper.updateByExampleSelective(diamondsinfo, diamondsinfoExample);
					}
					result.put("state", "success");
					result.put("message","These data had sent to audit");
				}else
				{
					result.put("state", "fail");
					result.put("message","There is no data to audit");
				}
			}else if(step.equals("auta"))
			{
				boolean checked = true;
				if(null != baseinfoList && baseinfoList.size() >0)
				{
					String message = "these bakset shoud be added[";
					Basketinfo basket = new Basketinfo();
					for(int i=0; i<baseinfoList.size(); i++)
					{
						basket =basketinfoMapper.selectByPrimaryKey(baseinfoList.get(i).getBasketno());
						if(basket.getStatus().equals(Constants.status_basket_audit_submit))
						{
							message = message+basket.getBasketno()+":";
							checked = false;
						}
						
					}
					message = message+"]";
					result.put("state", "fail");
					result.put("message",message);
					if(checked)
					{
						Diamondsinfo diamondsinfo = new Diamondsinfo();
						DiamondsinfoExample diamondsinfoExample = new DiamondsinfoExample();
						for(Basketinfo basketinfo : baseinfoList)
						{
							basketinfo.setStatus(Constants.status_basket_audit_auditer_submit);
							basketInfoServcie.updateBasketInfo(basketinfo);
							
							//更新钻石状态
							diamondsinfo.setBasketno(basketinfo.getBasketno());
							diamondsinfo.setStatus(basketinfo.getStatus());
							diamondsinfoExample.createCriteria().andBasketnoEqualTo(basketinfo.getBasketno());
							diamondsinfoMapper.updateByExampleSelective(diamondsinfo, diamondsinfoExample);
						}
						result.put("state", "success");
						result.put("message","Audit Success!");
					}
				}else
				{
					result.put("state", "fail");
					result.put("message","There is no data to submit");
				}
			
			}else
			{
				result.put("state", "fail");
				result.put("message","The step is not right");
			}
			
		}else
		{
			result.put("state", "fail");
			result.put("message","The step is not right");
		}
		return result.toString();
	}
}
