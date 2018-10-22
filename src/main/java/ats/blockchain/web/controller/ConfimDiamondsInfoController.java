package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.ResultUtil;

@Controller
public class ConfimDiamondsInfoController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private PackageInfoService packageInfoServcie;
	
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
	public String updateBasketInfo(PackageInfo packageInfo,String step)
	{
		logger.debug("BasketInfoController:basketinfo---->" + packageInfo.toString());
		String status = "";
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals(Constants.AOC_TO_GIA))
			{
				status = PackageState.AOC_REQ_LAB_VERIFY;
			}else if(step.equals(Constants.GIA_TO_AOC))
			{
				status = PackageState.LAB_ADD_VERIFY;
			}
		}
		packageInfo.setStatus(status);
		//packageInfo.setGiaapproveddate(DateFormatUtils.format(packageInfo.getGiaapproveddate(), "-"));
		boolean result = packageInfoServcie.labConfirmPackageInfo(packageInfo);
		String msg = "Add Success!";
		if(!result)
		{
			msg = "Add Failed";
		}
		return ResultUtil.msg(result, msg);

	}
	
	@RequestMapping("/confirm/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber,int pageSize,String step,HttpSession session)
	{
		List<String> statusList = new ArrayList<String>();
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals(Constants.AOC_TO_GIA))
			{
				statusList.add(PackageState.DMD_ISSUE);
				statusList.add(PackageState.AOC_REQ_LAB_VERIFY);
			}else if(step.equals(Constants.GIA_TO_AOC))
			{
				statusList.add(PackageState.AOC_SUBMIT_LAB_VERIFY);
				statusList.add(PackageState.LAB_ADD_VERIFY);
			}
		}
		
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list =packageInfoServcie.getPackageInfoByStatus(userid,statusList.toArray(new String[statusList.size()]));
		PagedObjectDTO result = new PagedObjectDTO();
		result.setRows(list = (list == null ? new ArrayList<PackageInfo>() : list));
		result.setTotal(Long.valueOf(list.size()));
		return result;
		
	}
	
	@RequestMapping("/confirm/submitBasketList")
	@ResponseBody
	public String submitBasketList(HttpSession session,String step) 
	{
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		//1，查询需要提交的basket信息

		if(StringUtils.isNotBlank(step))
		{
			List<PackageInfo> list = packageInfoServcie.submitPackageInfo(step,userid);
			if(AOCBeanUtils.isNotEmpty(list))
			{
				String message = "these data shoud check[";
				for(int i=0; i<list.size(); i++)
				{
					if(message.indexOf(list.get(i).getBasketno())==-1)
					{
						message = message+list.get(i).getBasketno()+":";
					}
					
				}
				message = message+"]";
				return ResultUtil.msg(false, message);
			}
			
		}
		logger.debug("submitBasketList end");
		return ResultUtil.msg(true, "Submit success");
	}
}
