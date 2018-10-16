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
public class TransferDiamondsInfoController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/*@Resource(name="basketInfoServcieImpl")
	private BasketInfoServcie basketInfoServcie;
	@Autowired
	private DiamondsinfoMapper diamondsinfoMapper;*/
	@Resource
	private PackageInfoService packageInfoServcie;
	
	
	@RequestMapping("/transfer/transferList")
	public String findBasketList()
	{
		return "transferList";
	}
	
	@RequestMapping("/transfer/transfervaultList")
	public String findConfirmgiaList()
	{
		return "transfervaultList";
	}
	
	
	@RequestMapping("/transfer/updateBasketInfo")
	@ResponseBody
	public String updateBasketInfo(PackageInfo packageInfo,String step)
	{
		logger.debug("BasketInfoController:basketinfo---->" + packageInfo.toString());
		String status = "";
		if(StringUtils.isNotBlank(step))
		{
			if(step.equals(Constants.AOC_TO_VAULT_OWNER))
			{
				status = PackageState.DMD_REQ_CHG_OWNER;
			}else if(step.equals(Constants.VAULT_OWNER_TO_AOC))
			{
				//status = Constants.status_basket_move_valut_into;
			}
		}
		packageInfo.setStatus(status);
		boolean result = packageInfoServcie.transferPackageInfo(packageInfo);
		String msg = "Add Success!";
		if(!result)
		{
			msg = "Add Failed";
		}
		return ResultUtil.msg(result, msg);

	}

	@RequestMapping("/transfer/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber,int pageSize,String step,HttpSession session) throws JSONException
	{
		List<String> statusList = new ArrayList<String>();

		if(StringUtils.isNotBlank(step))
		{
			if(step.equals(Constants.AOC_TO_VAULT_OWNER))
			{
				//11 14 15 17 22
				statusList.add(PackageState.VAULT_VERIFY_PASS);
				statusList.add(PackageState.AUDIT_VERIFY_PASS);
				statusList.add(PackageState.AUDIT_VERIFY_NOPASS);
				statusList.add(PackageState.DMD_REQ_CHG_OWNER);
				statusList.add(PackageState.DMD_CHANGE_OWNER_PASS);
			}else if(step.equals(Constants.VAULT_OWNER_TO_AOC))
			{
				//16
				statusList.add(PackageState.DMD_SUBMIT_CHG_OWNER);
			}
		}
		String userid= (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list =packageInfoServcie.getPackageInfoByStatus(userid,statusList.toArray(new String[statusList.size()]));
		PagedObjectDTO result = new PagedObjectDTO();
		result.setRows(list = (list == null ? new ArrayList<PackageInfo>() : list));
		result.setTotal(Long.valueOf(list.size()));
		return result;
		
	}
	
	@RequestMapping("/transfer/submitBasketList")
	@ResponseBody
	public String submitBasketList(@RequestBody BasketViewObject basketViewObject) throws JSONException
	{
		String step = basketViewObject.getStep() == null ? "" : basketViewObject.getStep();
		List<PackageInfo> baseinfoList = basketViewObject.getPackageInfos();
		if(StringUtils.isNotBlank(step))
		{
			List<PackageInfo> list = packageInfoServcie.submitPackageInfo(baseinfoList,step);
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
		return ResultUtil.msg(true, "These diamonds sumbmit success");
	}
}
