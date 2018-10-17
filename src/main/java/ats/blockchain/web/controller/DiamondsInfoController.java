package ats.blockchain.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.model.Product;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
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
	
	@RequestMapping("/checkGiaNo")
	@ResponseBody
	public String checkGiaNo(String giano,String tradeid,String userid) {
		logger.debug("DiamondsInfoController:checkGiaNo---->giano:" + giano + ",tradeid" + tradeid+",userid" + userid);
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
	public PagedObjectDTO getDiamondListClient(@RequestParam int pageNumber, int pageSize, HttpServletRequest request,HttpSession session) {
		
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(userid,PackageState.PKG_ISSUE,
				PackageState.DMD_CREATE);
		PagedObjectDTO dto = new PagedObjectDTO();
		dto.setRows(list);
		dto.setTotal((long) list.size());
		return dto;
	}

	@RequestMapping("/submitDiamondList")
	@ResponseBody
	public String submitDiamondList(HttpServletRequest request,HttpSession session) {
		logger.debug("submitDiamondList begin");
		// 1，查询需要提交的basket信息
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<DiamondInfoData> list = diamondsInfoService.submitDiamondList(userid);
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
		return ResultUtil.msg(true, "Sumbmit success");
	}

	@RequestMapping(value = "/importDiamondsInfo")
	@ResponseBody
	public String importDiamondsInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(session == null) {
			return ResultUtil.fail("Can not get session");
		}
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<DiamondInfoData> diamondsinfos = null;
		try {
			String filename = FileUtils.getFile(request, "diamondsinfo", "files");
			diamondsinfos = AOCBeanUtils.getObjectFromCsv(filename, DiamondInfoData.class);

			if (AOCBeanUtils.isEmpty(diamondsinfos)) {
				return ResultUtil.fail("import file is empty.");
			}

			boolean result = true;
			StringBuilder sb = new StringBuilder(128);
			sb.append("Import error:\n");
			Set<String> gianoSet =Sets.newHashSet();
			for (DiamondInfoData diamondInfoData : diamondsinfos) {
				diamondInfoData.setUserid(userid);
				String basketno = diamondInfoData.getBasketno();
				String giano = diamondInfoData.getGiano();
				List<DiamondInfoData> hist = diamondsInfoService.getDiamondInfoHistory(giano, basketno);
				if(gianoSet.contains(giano)) {
					sb.append("diamond giano duplicate: "+giano).append("\n");
					result = false;
					continue;
				}else {
					gianoSet.add(giano);
				}
				
				if(hist!=null && hist.size()>0) {
					logger.warn("duplicate diamond basketno: {}, giano: {}",basketno,giano);
					sb.append("diamond giano duplicate: "+giano).append("\n");
					result = false;
					continue;
				}
				
				String productcode = diamondInfoData.getProductcode();
				Product prod = getProduct(productcode);
				if(prod==null) {
					logger.error("can't find product of code {} ,basketno: {}",productcode,basketno);
					sb.append("unknown product code:").append(productcode).append("\n");
					result = false;
					continue;
				}
				BeanUtils.copyProperties(prod, diamondInfoData);

				Map<String, Object> rs = diamondsInfoService.addDiamondInfo(diamondInfoData);
				
				if (!ResultUtil.isSuccess(rs)) {
					sb.append(giano).append(":").append(ResultUtil.getMessage(rs)).append("\n");
					result = false;
					continue;
				}
			}
			return ResultUtil.msg(result, result ? "Import Success!" : sb.toString());
		} catch (Exception e) {
			logger.error("DiamondsInfoController--->importDiamondsInfo：" ,e);
			return ResultUtil.fail("import diamonds failed.");
		}
	}

	public Map<String, Object> getBasketMap(String userid) {
		Map<String, Object> basketMap = null;
		
		List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(userid,PackageState.PKG_ISSUE);
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
