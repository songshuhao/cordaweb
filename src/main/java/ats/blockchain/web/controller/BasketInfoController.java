package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.FileUtils;
import ats.blockchain.web.utils.ResultUtil;

@Controller
@RequestMapping("/basket")
public class BasketInfoController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private PackageInfoService basketInfoServcie;
	private final String aoc = "AOC";

	@RequestMapping("/addBasketInfo")
	@ResponseBody
	public String addBasketInfo(PackageInfo basketinfo){
		logger.debug("addBasketInfo: {}", JSON.toJSONString(basketinfo));
		boolean rs = false;
		rs = addPackageState(basketinfo);
		String msg = "Add Success!";
		if(!rs)
		{
			msg = "Add Failed";
		}
		logger.debug("BasketInfoController:basketinfo {} ,result: {}", basketinfo.getBasketno(),msg);
		return ResultUtil.msg(rs, msg);
		
	}

	private boolean addPackageState(PackageInfo basketinfo) {
		boolean rs;
		String aocLegalName = getUserLegalName(aoc);
		logger.debug("BasketInfoController: getUserLegalName for {} {}", aoc, aocLegalName);
		basketinfo.setAoc(aocLegalName);
		String supLegalName = getUserLegalName(basketinfo.getSuppliercode());
		logger.debug("BasketInfoController: getUserLegalName for supplier{}", supLegalName);
		basketinfo.setSuppliercode(supLegalName);
		rs = basketInfoServcie.addPackageInfo(basketinfo);
		return rs;
	}

	@RequestMapping("/findBasketList")
	public ModelAndView findDiamondList() {
		ModelAndView mac = new ModelAndView();
		mac.addObject("productList", this.getProductList());
		mac.setViewName("basketList");
		return mac;
	}

	@RequestMapping("/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber, int pageSize, HttpServletRequest request)
			throws JSONException {
		PagedObjectDTO dto = new PagedObjectDTO();
		List<PackageInfo> list = basketInfoServcie.getPackageInfoByStatus(PackageState.PKG_CREATE,PackageState.PKG_ISSUE);
		dto.setRows(list);
		dto.setTotal((long) list.size());
		return dto;
	}

	@RequestMapping("/submitBasketList")
	@ResponseBody
	public String submitBasketList(HttpServletRequest request)  
	{
		List<PackageInfo> list = basketInfoServcie.submitPackageByStatus(PackageState.PKG_CREATE);
		String rsStr="";
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
		logger.debug("submitBasketList end");
		return ResultUtil.msg(true, "These diamonds sumbmit success");
	}

	@RequestMapping(value = "/importBasketInfo")
	@ResponseBody
	public String importBasketInfo(HttpServletRequest request) {
		List<PackageInfo> basketinfos = null;
		try
		{
			String filename = FileUtils.getFile(request, "baskeinfo","files");
			basketinfos = AOCBeanUtils.getObjectFromCsv(filename, PackageInfo.class);
		} catch (Exception e)
		{
			logger.error("importBasketInfo error", e);
			return ResultUtil.fail("file is invaild.");
		}
		
		if(basketinfos==null || basketinfos.isEmpty()) {
			return ResultUtil.fail("import file is empty.");
		}
		List<PackageInfo> addFailList = new ArrayList<>();
		for(PackageInfo bsk :basketinfos) {
			if(!addPackageState(bsk)) {
				logger.error("importBasketInfo to corda error: {} ",bsk);
				addFailList.add(bsk);
			}
		}
		String resultStr = "";
		if(addFailList.size()>0) {
			resultStr = ResultUtil.fail("add package failded", addFailList);
		}else {
			resultStr = ResultUtil.success();
		}
		
		return resultStr;
	}
}
