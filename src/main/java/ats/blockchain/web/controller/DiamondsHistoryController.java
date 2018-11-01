package ats.blockchain.web.controller;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.PackageInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.CustSort;

@Controller
@RequestMapping("/history")
public class DiamondsHistoryController extends BaseController {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private PackageInfoService packageInfoService;

	@RequestMapping("/historyList")
	public String findBasketList() {
		return "historyList";
	}
	private CustSort<PackageInfo> compare = new CustSort<PackageInfo>();
	/**
	 * 
	 * @param pageNumber 页码
	 * @param pageSize 每页记录数
	 * @param limit 每页记录数
	 * @param offset 索引
	 * @param order 排序规则:asc desc
	 * @param sort 排序字段
	 * @param search 查询条件(basketno)
	 * @param request
	 * @return
	 */
	@RequestMapping("/getDiamondList")
	@ResponseBody
	public PagedObjectDTO getDiamondListClient(int pageNumber, int pageSize, Integer limit, Integer offset,
			String order, String sort, String search, HttpServletRequest request) {
		logger.debug("DiamondsHistoryController----->pageNumber:" + pageNumber + ";pageSize:" + pageSize + ";limit:"
				+ limit + ";offset:" + offset + ";order:" + order + ";sort:" + sort + ";search:" + search);
		//offset = offset + 1;
		// List<DiamondInfoData> diamondsinfos =
		// diamondsInfoService.getDiamondInfoData(offset,limit);
		List<PackageInfo> pkgList = null;
		long total =0l;
		if(StringUtils.isNotBlank(search)) {
			pkgList = packageInfoService.getPackageInfoById(pageNumber, pageSize, search);
			total =  packageInfoService.getPackageInfoNumById(search);
		}else {
			pkgList = packageInfoService.getPackageInfo(pageNumber, pageSize);
			total = packageInfoService.getPackageInfoNum();
		}
		
		compare.setAsc("asc".equals(order));
		compare.setSortField(sort);
		Collections.sort(pkgList,compare);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		pagedObjectDTO.setRows(pkgList);
		pagedObjectDTO.setTotal(total);
		return pagedObjectDTO;
	}

	@RequestMapping("/getDiamondDetails")
	@ResponseBody
	public String getDiamondDetails(@RequestParam int pageNumber, int pageSize, String basketno) throws JSONException {
		List<PackageAndDiamond> packageInfos = packageInfoService.getPackageAndDiamondById(basketno);
		List<DiamondInfoData> diamondsinfos = packageInfos.get(0).getDiamondList();
		PackageInfo basketinfo = packageInfos.get(0).getPkgInfo();
		JSONObject result = new JSONObject();
		if (null != basketinfo) {
			result.put("basketInfo", JSON.toJSON(basketinfo));
		} else {
			result.put("basketInfo", JSON.toJSON(new Basketinfo()));
		}
		if (AOCBeanUtils.isNotEmpty(diamondsinfos)) {
			result.put("rows", JSON.toJSON(diamondsinfos));
			result.put("total", JSON.toJSON(diamondsinfos.size()));

		} else {
			result.put("rows", JSON.toJSON(Lists.newArrayList()));
			result.put("total", JSON.toJSON(0));
		}
		return result.toString();
	}

	@RequestMapping("/getDiamondsHistoryList")
	@ResponseBody
	public PagedObjectDTO getDiamondsHistoryList(@RequestParam int pageNumber, int pageSize, String giano,
			String basketno) {
		List<DiamondInfoData> diamondsinfos = diamondsInfoService.getDiamondInfoHistory(giano, basketno);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		pagedObjectDTO.setRows(diamondsinfos);
		pagedObjectDTO.setTotal(Long.valueOf(diamondsinfos.size()));
		return pagedObjectDTO;

	}
}
