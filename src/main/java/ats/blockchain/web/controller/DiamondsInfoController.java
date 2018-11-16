package ats.blockchain.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.cache.CacheFactory;
import ats.blockchain.web.cache.DiamondCache;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.FileUtils;
import ats.blockchain.web.utils.ResultUtil;

@Controller
@RequestMapping("/diamond")
public class DiamondsInfoController extends BaseController {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(DiamondsInfoController.class);

	@RequestMapping("/addDiamondInfo")
	@ResponseBody
	public String addDiamondInfo(DiamondInfoData diamondsinfo) {
		if(diamondsinfo == null)
		{
			ResultUtil.fail("Add Diamonds failed!");
		}
		logger.debug("DiamondsInfoController:diamondsinfo---->" + diamondsinfo.toString());
		
		String userid = diamondsinfo.getUserid();
		String basketno = diamondsinfo.getBasketno();
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);
		PackageAndDiamond pad = cache.getDiamond(basketno, PackageState.PKG_ISSUE);
		PackageInfo pkg = pad.getPkgInfo();
		List<DiamondInfoData> oldlist = pad.getDiamondList();
		List<DiamondInfoData> tmplist = Lists.newArrayList();
		if (oldlist != null) {
			tmplist.addAll(oldlist);
		}

		tmplist.add(diamondsinfo);
		try {
			AOCBeanUtils.checkDiamond(pkg, tmplist);
		} catch (DiamondWebException e) {
			logger.error("addDiamondInfo error,gia no: " + diamondsinfo.getGiano(), e);
			return ResultUtil.fail(e.getMessage());
		}

		Map<String, Object> rs = diamondsInfoService.addDiamondInfo(diamondsinfo);
		logger.debug("addDiamondInfo: {},giano: {}, result: {}", diamondsinfo.getBasketno(), diamondsinfo.getGiano(),
				rs);
		return ResultUtil.parseMap(rs);
	}

	@RequestMapping("/checkGiaNo")
	@ResponseBody
	public String checkGiaNo(String giano, String tradeid, String userid) {
		logger.debug(
				"DiamondsInfoController:checkGiaNo---->giano:" + giano + ",tradeid" + tradeid + ",userid" + userid);
		Map<String, Object> rs = new HashMap<String, Object>();
		rs = diamondsInfoService.checkGiano(userid, tradeid, giano);
		return ResultUtil.parseMap(rs);
	}

	@RequestMapping("/eidtDiamondInfo")
	@ResponseBody
	public String eidtDiamondInfo(DiamondInfoData diamondsinfo) {
		logger.debug("DiamondsInfoController:diamondsinfo---->" + diamondsinfo.toString());

		Map<String, Object> rs = diamondsInfoService.editDiamondInfo(diamondsinfo);
		logger.debug("eidtDiamondInfo: {},giano: {}, result: {}", diamondsinfo.getBasketno(), diamondsinfo.getGiano(),
				rs);

		return ResultUtil.parseMap(rs);
	}

	@RequestMapping("/deleteDiamondInfo")
	@ResponseBody
	public String deleteDiamondInfo(DiamondInfoData diamondsinfo) {
		logger.debug("DiamondsInfoController:diamondsinfo---->" + diamondsinfo.toString());

		Map<String, Object> rs = diamondsInfoService.deleteDiamondInfo(diamondsinfo);
		logger.debug("addDiamondInfo: {},giano: {}, result: {}", diamondsinfo.getBasketno(), diamondsinfo.getGiano(),
				rs);

		return ResultUtil.parseMap(rs);
	}

	@RequestMapping("/findDiamondList")
	public ModelAndView findDiamondList(HttpSession session) {
		ModelAndView mac = new ModelAndView();
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		Map<String, Object> basketMap = this.getBasketMap(userid);
		mac.addObject("basketMap", basketMap);
		mac.setViewName("diamondsList");
		return mac;
	}

	@RequestMapping("/getDiamondList")
	@ResponseBody
	public PagedObjectDTO getDiamondListClient(@RequestParam int pageNumber, int pageSize, HttpServletRequest request,
			HttpSession session) {

		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(userid, PackageState.PKG_ISSUE,
				PackageState.DMD_CREATE);
		PagedObjectDTO dto = new PagedObjectDTO();
		dto.setRows(list);
		dto.setTotal((long) list.size());
		return dto;
	}

	@RequestMapping("/submitDiamondList")
	@ResponseBody
	public String submitDiamondList(HttpServletRequest request, HttpSession session) {
		logger.debug("submitDiamondList begin");
		// 1，查询需要提交的basket信息
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(userid, PackageState.PKG_ISSUE);
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
		list = diamondsInfoService.submitDiamondList(userid);
		logger.debug("submitDiamondList end");
		if (AOCBeanUtils.isNotEmpty(list)) {
			StringBuilder message = new StringBuilder();
			message.append("These diamond submit failed:[");
			list.forEach(d -> message.append(d.getGiano()).append(":"));
			String msg = message.substring(0, message.length() - 1);
			msg = msg + "]";
			return ResultUtil.msg(false, msg);
		} else {
			return ResultUtil.msg(true, "Submit success");
		}

	}

	@RequestMapping(value = "/importDiamondsInfo")
	@ResponseBody
	public String importDiamondsInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session == null) {
			return ResultUtil.fail("Can not get session");
		}
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		// String step = "";
		List<DiamondInfoData> diamondsinfos = null;
		try {
			diamondsinfos = FileUtils.getFile(request, htmlfileName, DiamondInfoData.class);
			if (AOCBeanUtils.isEmpty(diamondsinfos)) {
				return ResultUtil.fail("import file is empty.");
			}
			// 刷新diamondcache,将新录入的package信息更新到cache
			diamondsInfoService.getDiamondInfoByStatus(userid, PackageState.PKG_ISSUE);
			boolean result = true;
			StringBuilder sb = new StringBuilder(128);
			Set<String> failSet = Sets.newHashSet();
			Map<String, List<DiamondInfoData>> importMap = Maps.newHashMap();

			sb.append("Import error:\n");
			for (DiamondInfoData diamondInfoData : diamondsinfos) {
				diamondInfoData.setUserid(userid);
				Map<String, Object> rs = checkImport(diamondInfoData);
				if (!ResultUtil.isSuccess(rs)) {
					sb.append(ResultUtil.getMessage(rs));
					result = false;
					continue;
				}
				String basketno = diamondInfoData.getBasketno();

				String productcode = diamondInfoData.getProductcode();
				Product prod = getProduct(productcode);
				if (prod == null) {
					logger.error("can't find product of code {} ,basketno: {}", productcode, basketno);
					sb.append("unknown product code:").append(productcode).append("\n");
					result = false;
					continue;
				}
				BeanUtils.copyProperties(prod, diamondInfoData);
				String tradeid = diamondInfoData.getTradeid();

				if (StringUtils.isNotBlank(tradeid)) {
					logger.error("tradeid must be null ,basketno: {}", basketno);
					sb.append("tradeid must be null\n");
					result = false;
					continue;
				}
				List<DiamondInfoData> diList = null;
				if (importMap.containsKey(basketno)) {
					diList = importMap.get(basketno);
				} else {
					diList = Lists.newArrayList();
					importMap.put(basketno, diList);
				}
				diList.add(diamondInfoData);
			}
			if (!result) {
				String string = sb.toString();
				return ResultUtil.msg(result, string);
			}
			DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);

			for (Entry<String, List<DiamondInfoData>> ent : importMap.entrySet()) {
				String basketno = ent.getKey();
				PackageAndDiamond pad = cache.getDiamond(basketno, PackageState.PKG_ISSUE);
				PackageInfo pkg = pad.getPkgInfo();
				List<DiamondInfoData> diamondList = ent.getValue();
				logger.debug("check diamond in package: {}", pkg.getBasketno());
				try {
					AOCBeanUtils.checkDiamond(pkg, diamondList);
				} catch (DiamondWebException e) {
					sb.append(e.getMessage());
					result = false;
					continue;
				}
				logger.debug("check diamond in package: {} pass.", pkg.getBasketno());
				// 检查通过 添加钻石
				diamondList.forEach(d -> {
					Map<String, Object> rs = diamondsInfoService.addDiamondInfo(d);
					if (!ResultUtil.isSuccess(rs)) {
						sb.append(basketno).append(", GIA no:").append(d.getGiano()).append(", ")
								.append(ResultUtil.getMessage(rs)).append("\n");
						failSet.add(basketno);
					}
				});
			}

			return ResultUtil.msg(result, result ? "Import Success!" : sb.toString());
		} catch (Exception e) {
			logger.error("DiamondsInfoController--->importDiamondsInfo：", e);
			return ResultUtil.fail("import diamonds failed:" + e.getMessage());
		}
	}

	public Map<String, Object> getBasketMap(String userid) {
		Map<String, Object> basketMap = null;

		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(userid, PackageState.PKG_ISSUE);
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

	private static Map<String, Object> checkImport(DiamondInfoData inf) {
		logger.debug("check import diamond: {} {}", inf.getBasketno(), inf.getGiano());
		StringBuilder msg = new StringBuilder();
		String userid = inf.getUserid();
		DiamondCache cache = CacheFactory.Instance.getDiamondCache(userid);

		String basketno = inf.getBasketno();
		if (StringUtils.isBlank(basketno)) {
			msg.append("packageno is empty \n");
			logger.warn("package {} import check failed: {}", basketno, msg.toString());
			return ResultUtil.failMap(msg.toString());
		}
		if (StringUtils.isBlank(inf.getGiano())) {
			msg.append("GIA No. is empty \n");
			logger.warn("package {} import check failed: {}", basketno, msg.toString());
			return ResultUtil.failMap(msg.toString());
		}
		msg.append("import diamond ").append(inf.getGiano()).append(" error: ");
		
		PackageAndDiamond pad = cache.getDiamond(basketno, PackageState.PKG_ISSUE);
		if (pad == null) {
			msg.append("package ").append(basketno).append(" does not exist or has been full.\n");
			logger.warn("package {} import check failed: {}", basketno, msg.toString());
			return ResultUtil.failMap(msg.toString());
		}

		List<DiamondInfoData> list = pad.getDiamondList();
		if (list != null && list.size() > 0) {
			msg.append("package ").append(basketno).append(" has diamond,don't support import.\n");
			logger.warn("package {} import check failed: {}", basketno, msg.toString());
			return ResultUtil.failMap(msg.toString());
		}

		PackageAndDiamond pad1 = cache.getDiamond(basketno, PackageState.DMD_CREATE);
		if (pad1 != null) {
			msg.append("package ").append(basketno).append(" has diamond,don't support import.\n");
			logger.warn("package {} import check failed: {}", basketno, msg.toString());
			return ResultUtil.failMap(msg.toString());
		}

		String status = inf.getStatus();
		if (StringUtils.isBlank(status)) {
			inf.setStatus(PackageState.PKG_ISSUE);
		}
		logger.info("check single diamond pass ,basketno : {},giano: {}",basketno,inf.getGiano());
		return ResultUtil.msgMap(true, "");
	}
}
