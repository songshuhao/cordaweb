package ats.blockchain.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.FileUtils;
import ats.blockchain.web.utils.ResultUtil;

@Controller
@RequestMapping("/diamond")
public class DiamondsInfoController extends BaseController {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/addDiamondInfo")
	@ResponseBody
	public String addDiamondInfo(DiamondInfoData diamondsinfo) {
		logger.debug("DiamondsInfoController:diamondsinfo---->" + diamondsinfo.toString());

		Map<String, Object> rs = diamondsInfoService.addDiamondInfo(diamondsinfo);
		logger.debug("addDiamondInfo: {},giano: {}, result: {}", diamondsinfo.getBasketno(), diamondsinfo.getGiano(),
				rs);

		return ResultUtil.parseMap(rs);
	}

	@RequestMapping("/findDiamondList")
	public ModelAndView findDiamondList() {
		ModelAndView mac = new ModelAndView();
		Map<String, Object> basketMap = this.getBasketMap();
		mac.addObject("basketMap", basketMap);
		mac.setViewName("diamondsList");
		return mac;
	}

	@RequestMapping("/getDiamondList")
	@ResponseBody
	public PagedObjectDTO getDiamondListClient(@RequestParam int pageNumber, int pageSize, HttpServletRequest request) {
		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(PackageState.PKG_ISSUE,
				PackageState.DMD_CREATE);
		PagedObjectDTO dto = new PagedObjectDTO();
		dto.setRows(list);
		dto.setTotal((long) list.size());
		return dto;
	}

	@RequestMapping("/submitDiamondList")
	@ResponseBody
	public String submitDiamondList(HttpServletRequest request) throws JSONException {
		logger.debug("submitDiamondList begin");
		// 1，查询需要提交的basket信息
		List<DiamondInfoData> list = diamondsInfoService.submitDiamondList();
		if (AOCBeanUtils.isNotEmpty(list)) {
			String message = "These bakset is not fill to the full diamonds[";
			for (int i = 0; i < list.size(); i++) {
				if (message.indexOf(list.get(i).getBasketno()) == -1) {
					message = message + list.get(i).getBasketno() + ":";
				}

			}
			message = message + "]";
			return ResultUtil.msg(false, message);
		}
		logger.debug("submitDiamondList end");
		return ResultUtil.msg(true, "These diamonds sumbmit success");
	}

	@RequestMapping(value = "/importDiamondsInfo")
	@ResponseBody
	public String importDiamondsInfo(HttpServletRequest request) throws JSONException {
		List<DiamondInfoData> diamondsinfos = null;
		String message = "";
		try {
			String filename = FileUtils.getFile(request, "diamondsinfo", "files");
			diamondsinfos = AOCBeanUtils.getObjectFromCsv(filename, DiamondInfoData.class);
		} catch (Exception e) {
			logger.error("DiamondsInfoController--->importDiamondsInfo：" + e.getMessage());
			e.printStackTrace();
		}

		if (AOCBeanUtils.isEmpty(diamondsinfos)) {
			return ResultUtil.fail("import file is empty.");
		}

		boolean result = true;
		message = "";
		for (DiamondInfoData diamondInfoData : diamondsinfos) {
			Map<String, Object> rs = diamondsInfoService.addDiamondInfo(diamondInfoData);
			if (!ResultUtil.isSuccess(rs)) {
				message = message + " Import error:[" + diamondInfoData.getGiano() + "] \n";
				result = false;
			}
		}
		return ResultUtil.msg(result, result ? "Import Success!" : message);
	}

	public Map<String, Object> getBasketMap() {
		Map<String, Object> basketMap = null;
		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(PackageState.PKG_ISSUE);
		if (list != null) {
			basketMap = new HashMap<String, Object>();
			for (DiamondInfoData l : list) {
				basketMap.put(l.getBasketno(), l.getBasketno());
			}
			logger.debug("getBasketMap :{}", basketMap);
		} else {
			logger.debug("basket is null");
			basketMap = Collections.emptyMap();
		}

		return basketMap;
	}

}
