package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.model.BasketViewObject;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.ResultUtil;

@Controller
public class AuditDiamondsInfoController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private PackageInfoService packageInfoServcie;
	
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
	public String updateBasketInfo(PackageInfo packageInfo,String step) throws JSONException
	{
		logger.debug("BasketInfoController:basketinfo---->" + packageInfo.toString());
		String status = "";
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals(Constants.AOC_TO_AUDIT))
			{
				
			}else if(step.equals(Constants.AUDIT_TO_AOC))
			{
				status = PackageState.AUDIT_ADD_VERIFY;
			}
		}
		packageInfo.setStatus(status);
		boolean result = packageInfoServcie.auditPackageInfo(packageInfo);
		String msg = "Add Success!";
		if(!result)
		{
			msg = "Add Failed";
		}
		return ResultUtil.msg(result, msg);

	}
	
	@RequestMapping("/audit/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber,int pageSize,String step,HttpServletRequest request) throws JSONException
	{
		List<String> statusList = new ArrayList<String>();
		
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals(Constants.AOC_TO_AUDIT))
			{
				statusList.add(PackageState.VAULT_VERIFY_PASS);
				statusList.add(PackageState.DMD_CHANGE_OWNER_PASS);
				statusList.add(PackageState.AUDIT_VERIFY_PASS);
				statusList.add(PackageState.AUDIT_VERIFY_NOPASS);
			}else if(step.equals(Constants.AUDIT_TO_AOC))
			{
				statusList.add(PackageState.AOC_REQ_AUDIT_VERIFY);
				statusList.add(PackageState.AUDIT_ADD_VERIFY);
			}
		}
		String userid = (String) request.getSession().getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list = packageInfoServcie.getPackageStateWithoutRedeemByStatus(userid,redeemOwnerId, statusList.toArray(new String[statusList.size()]));
		PagedObjectDTO result = new PagedObjectDTO();
		result.setRows(list = (list == null ? new ArrayList<PackageInfo>() : list));
		result.setTotal(Long.valueOf(list.size()));
		return result;
		
	}
	
	@RequestMapping(value="/audit/submitBasketList")
	@ResponseBody
	public String submitBasketList(@RequestBody BasketViewObject basketViewObject,HttpSession session) throws JSONException
	{
		String step = basketViewObject.getStep() == null ? "" : basketViewObject.getStep();
		List<PackageInfo> baseinfoList = basketViewObject.getPackageInfos();
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		if(StringUtils.isNotBlank(step))
		{
			List<PackageInfo> list = packageInfoServcie.submitPackageInfo(baseinfoList,step,userid);
			if(AOCBeanUtils.isNotEmpty(list))
			{
				String message = "these data shoud be check[";
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
		return ResultUtil.msg(true, "Sumbmit success");
	}
}
