package ats.blockchain.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.DiamondsinfoExample;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;

@Controller
@RequestMapping("/history")
public class DiamondsHistoryController extends BaseController {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private PackageInfoService packageInfoServcie;
	
	@RequestMapping("/historyList")
	public String findBasketList()
	{
		return "historyList";
	}
	
	@RequestMapping("/getDiamondList")
	@ResponseBody
	public PagedObjectDTO getDiamondListClient(@RequestParam int pageNumber, int pageSize, HttpServletRequest request) {

		List<DiamondInfoData> diamondsinfos = diamondsInfoService.getDiamondInfoData();
		logger.debug("DiamondsHistoryController:diamondsinfos---->" + diamondsinfos.toString());
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		pagedObjectDTO.setRows(diamondsinfos);
		pagedObjectDTO.setTotal(Long.valueOf(diamondsinfos.size()));
		return pagedObjectDTO;

	}

	@RequestMapping("/getDiamondDetails")
	@ResponseBody
	public String getDiamondDetails(@RequestParam int pageNumber, int pageSize,String basketno) throws JSONException {
		List<PackageAndDiamond> packageInfos = packageInfoServcie.getPackageAndDiamondById(basketno);
		List<DiamondInfoData> diamondsinfos = packageInfos.get(0).getDiamondList();
		PackageInfo basketinfo= packageInfos.get(0).getPkgInfo();
		JSONObject result = new JSONObject();
		if(null != basketinfo)
		{
			result.put("basketInfo", JSON.toJSON(basketinfo));
		}else
		{
			result.put("basketInfo", JSON.toJSON(new Basketinfo()));
		}
		if (AOCBeanUtils.isNotEmpty(diamondsinfos)) {
			result.put("rows", JSON.toJSON(diamondsinfos));
			result.put("total", JSON.toJSON(diamondsinfos.size()));
			
		}else
		{
			result.put("rows", JSON.toJSON(Lists.newArrayList()));
			result.put("total", JSON.toJSON(0));
		}
		return result.toString();
	}

	
	@RequestMapping("/getDiamondsHistoryList")
	@ResponseBody
	public PagedObjectDTO getDiamondsHistoryList(@RequestParam int pageNumber, int pageSize,String giano,String basketno) {
		List<DiamondInfoData> diamondsinfos = diamondsInfoService.getDiamondInfoHistory(giano,basketno);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		pagedObjectDTO.setRows(diamondsinfos);
		pagedObjectDTO.setTotal(Long.valueOf(diamondsinfos.size()));
		return pagedObjectDTO;

	}
}
