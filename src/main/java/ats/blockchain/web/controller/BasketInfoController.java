package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import ats.blockchain.web.dao.DiamondsinfoMapper;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.BasketinfoExample;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.BasketInfoServcie;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.FileUtils;

@Controller
@RequestMapping("/basket")
public class BasketInfoController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource(name="basketInfoServcieImpl")
	private BasketInfoServcie basketInfoServcie;
	@Autowired
	private DiamondsinfoMapper diamondsinfoMapper;
	
	@RequestMapping("/addBasketInfo")
	@ResponseBody
	public String addBasketInfo(Basketinfo basketinfo) throws JSONException
	{
		logger.debug("BasketInfoController:basketinfo---->{}" , basketinfo.toString());
		
		JSONObject result = new JSONObject();
		try
		{
			//statusId=0,basketinfo录入
			basketinfo.setStatus(Constants.status_basket_into);
			basketInfoServcie.addBasketInfo(basketinfo);
			result.put("state", "success");
		} catch (Exception e)
		{
			logger.error("BasketInfoController--->addBasketInfo：" + e.getMessage());
			result.put("state", "fail");
			result.put("message",e.getMessage().toString());
			e.printStackTrace();
		}
		return result.toString();

	}
	
	
	@RequestMapping("/findBasketList")
	public ModelAndView findBasketList() {
		ModelAndView mac = new ModelAndView();
		mac.addObject("productList", this.getProductList());
		mac.setViewName("basketList");
		return mac;
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
	
	@RequestMapping("/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber,int pageSize,HttpServletRequest request) throws JSONException
	{
		BasketinfoExample example = new BasketinfoExample();
		List<Basketinfo> list =basketInfoServcie.getBasketList(example);
		PagedObjectDTO result = new PagedObjectDTO();
		result.setRows(list == null ? new ArrayList<Basketinfo>() : list);
		result.setTotal(new Long(list.size()));
		return result;
		
	}
	
	@RequestMapping("/submitBasketList")
	@ResponseBody
	public String submitBasketList(HttpServletRequest request) throws JSONException
	{
		//1，查询需要提交的basket信息
		BasketinfoExample example = new BasketinfoExample();
		example.createCriteria().andStatusEqualTo(Constants.status_basket_into);
		List<Basketinfo> list =basketInfoServcie.getBasketList(example);
		JSONObject result = new JSONObject();
		
		try
		{
			if(null != list && list.size() >0)
			{
				//2，调用cordaAPI进行区块链存储
				
				//3，存储成功之后更新basket信息
				for(Basketinfo basketinfo : list)
				{
					basketinfo.setStatus(Constants.status_basket_submit);
					basketInfoServcie.updateBasketInfo(basketinfo);
				}
				//4,同步信息到diamonds表
				Diamondsinfo diamondsinfo = new Diamondsinfo();
				for(Basketinfo basketinfo : list)
				{
					for(int i=0; i<basketinfo.getDiamondsnumber(); i++)
					{
						diamondsinfo.setBasketno(basketinfo.getBasketno());
						diamondsinfo.setSupcode(basketinfo.getSuppliercode());
						diamondsinfo.setSupname(basketinfo.getSuppliername());
						diamondsinfo.setProductcode(basketinfo.getProductcode());
						diamondsinfo.setStatus(Constants.status_basket_submit);
						diamondsinfoMapper.insertSelective(diamondsinfo);
					}
				}
				result.put("state", "success");
			}else
			{
				result.put("state", "fail");
				result.put("message","There is no data to submit");
			}
		} catch (Exception e)
		{
			logger.error("BasketInfoController--->submitBasketList：" + e.getMessage());
			result.put("state", "fail");
			result.put("message",e.getMessage().toString());
			//e.printStackTrace();
		}
		return result.toString();
		
	}
	
	
	@RequestMapping(value="/importBasketInfo")
	@ResponseBody
	public String importBasketInfo(HttpServletRequest request)
	{
		List<Basketinfo> basketinfos = null;
		try
		{
			String filename = FileUtils.getFile(request, "baskeinfo","files");
			basketinfos = AOCBeanUtils.getObjectFromCsv(filename, Basketinfo.class);
		} catch (Exception e)
		{
			logger.error("BasketInfoController--->submitBasketList：" + e.getMessage());
			e.printStackTrace();
		}
		
        JSONObject result = new JSONObject(); 
		try
		{
			if(null != basketinfos && basketinfos.size() > 0)
			{
				for(Basketinfo basketinfo : basketinfos)
				{
					basketInfoServcie.addBasketInfo(basketinfo);
				}
				result.put("state", "success");
				result.put("message","Import baksetinfo all right");
			}
			
		} catch (Exception e)
		{
			logger.error("BasketInfoController--->importBasketInfo：" + e.getMessage());
			try
			{
				result.put("state", "fail");
				result.put("message",e.getMessage().toString());
			} catch (JSONException e1)
			{
				e1.printStackTrace();
			}
		}
		return result.toString();
	}
}
