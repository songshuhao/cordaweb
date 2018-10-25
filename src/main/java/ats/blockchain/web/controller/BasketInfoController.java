package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ats.blockchain.web.utils.Constants;
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
	public String addBasketInfo(PackageInfo basketinfo) {
		logger.debug("addBasketInfo: {}", JSON.toJSONString(basketinfo));
		Map<String, Object> rs = addPackageState(basketinfo);
		logger.debug("BasketInfoController:basketinfo {} ,result: {}", basketinfo.getBasketno(), rs);
		return ResultUtil.parseMap(rs);

	}

	private Map<String, Object> addPackageState(PackageInfo basketinfo) {
		Map<String, Object> rs = null;
		String aocLegalName = null;
		String supLegalName = null;
		try {
			aocLegalName = getUserLegalName(aoc);
			supLegalName = getUserLegalName(basketinfo.getSuppliercode());
		} catch (Exception e) {
			String message = e.getMessage();
			return ResultUtil.failMap(message);
		}
		logger.debug("BasketInfoController: getUserLegalName for {} {}", aoc, aocLegalName);
		basketinfo.setAoc(aocLegalName);
		logger.debug("BasketInfoController: getUserLegalName for supplier{}", supLegalName);
		basketinfo.setSuppliercode(supLegalName);
		String seqNo = basketinfo.getSeqNo();
		if (StringUtils.isBlank(seqNo)) {
			logger.debug("addPackageState seqNo :{},basketno : {}", seqNo,basketinfo.getBasketno());
			rs = basketInfoServcie.addPackageInfo(basketinfo);
		} else {
			logger.debug("editPackageState seqNo :{},basketno : {}", seqNo,basketinfo.getBasketno());
			rs = basketInfoServcie.editPackageInfo(basketinfo);
		}
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
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber, int pageSize, HttpSession session) {
		PagedObjectDTO dto = new PagedObjectDTO();
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list = basketInfoServcie.getPackageInfoByStatus(userid,
				new String[] { PackageState.PKG_CREATE });
		dto.setRows(list);
		dto.setTotal((long) list.size());
		return dto;
	}

	@RequestMapping("/submitBasketList")
	@ResponseBody
	public String submitBasketList(HttpSession session) {
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list = basketInfoServcie.submitPackageByStatus(PackageState.PKG_CREATE, userid);
		if (AOCBeanUtils.isNotEmpty(list)) {
			String message = "these data shoud be check[";
			for (int i = 0; i < list.size(); i++) {
				if (message.indexOf(list.get(i).getBasketno()) == -1) {
					message = message + list.get(i).getBasketno() + ":";
				}
			}
			message = message + "]";
			return ResultUtil.msg(false, message);
		}
		logger.debug("submitBasketList end");
		return ResultUtil.msg(true, "Submit success");
	}

	@RequestMapping(value = "/importBasketInfo")
	@ResponseBody
	public String importBasketInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session == null) {
			return ResultUtil.fail("Can not get session");
		}
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> basketinfos = null;
		try {
			basketinfos = FileUtils.getFile(request, "baskeinfo", "files", PackageInfo.class);
		} catch (Exception e) {
			logger.error("importBasketInfo error", e);
			return ResultUtil.fail("file is invaild.");
		}
		if (basketinfos == null || basketinfos.isEmpty()) {
			logger.error("import file is empty.");
			return ResultUtil.fail("import file is empty.");
		}
		logger.debug("importBasketInfo read PackageInfo from file number:{}",basketinfos.size());
		
		List<PackageInfo> addFailList = new ArrayList<>();
		String message = "Check package failded:\n";
		for (PackageInfo bsk : basketinfos) {
			bsk.setUserid(userid);
			logger.debug("importBasketInfo check :{}",bsk.getBasketno());
			
			Map<String, Object> check = AOCBeanUtils.checkPackage(bsk);
			if (!ResultUtil.isSuccess(check)) {
				Object err = check.get("message");
				logger.error("importBasketInfo to corda error ,baksetno:{} ,error: {}", bsk, err);
				message = message + err + "\n";
				addFailList.add(bsk);
			}
		}
		if (addFailList.size() > 0) {
			return ResultUtil.fail(message, addFailList);
		}

		for (PackageInfo bsk : basketinfos) {
			logger.debug("importBasketInfo add :{}",bsk.getBasketno());
			Map<String, Object> map = addPackageState(bsk);
			if (!ResultUtil.isSuccess(map)) {
				logger.error("importBasketInfo to corda error: {} ", bsk);
				message = message + map.get("message") + "\n";
				addFailList.add(bsk);
			}
		}

		String resultStr = "";
		if (addFailList.size() > 0) {
			resultStr = ResultUtil.fail("Add package failded:\n" + message, addFailList);
		} else {
			resultStr = ResultUtil.success();
		}

		return resultStr;
	}
}
