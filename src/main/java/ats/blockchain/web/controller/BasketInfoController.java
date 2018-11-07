package ats.blockchain.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.ExportConfig;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.config.DiamondApplicationRunner;
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
	private PackageInfoService packageInfoService;
	private final String aoc = "AOC";
	@Value("${exportPath}")
	private String tmpFilePath;

	private static final String diamondPrefix = "diamond";
	private static final String packagePrefix = "package";

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
			supLegalName =basketinfo.getSuppliercode();
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
			logger.debug("addPackageState seqNo :{},basketno : {}", seqNo, basketinfo.getBasketno());
			rs = packageInfoService.addPackageInfo(basketinfo);
		} else {
			logger.debug("editPackageState seqNo :{},basketno : {}", seqNo, basketinfo.getBasketno());
			rs = packageInfoService.editPackageInfo(basketinfo);
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

	@RequestMapping("/checkPackageNo")
	@ResponseBody
	public String checkPackageNo(String basketno, String seqno, String userid) {
		logger.debug("BasketInfoController: checkPackageNo---->basketno:" + basketno + ",seqno" + seqno + ",userid"
				+ userid);
		Map<String, Object> rs = new HashMap<String, Object>();
		rs = packageInfoService.checkPackageNo(userid, seqno, basketno);
		return ResultUtil.parseMap(rs);
	}

	@RequestMapping("/getBasketList")
	@ResponseBody
	public PagedObjectDTO getBasketListClient(@RequestParam int pageNumber, int pageSize, HttpSession session) {
		PagedObjectDTO dto = new PagedObjectDTO();
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list = packageInfoService.getPackageInfoByStatus(userid,
				new String[] { PackageState.PKG_CREATE });
		dto.setRows(list);
		dto.setTotal((long) list.size());
		return dto;
	}

	@RequestMapping("/submitBasketList")
	@ResponseBody
	public String submitBasketList(HttpSession session) {
		String userid = (String) session.getAttribute(Constants.SESSION_USER_ID);
		List<PackageInfo> list = packageInfoService.submitPackageByStatus(PackageState.PKG_CREATE, userid);
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
			basketinfos = FileUtils.getFile(request, htmlfileName, PackageInfo.class);
		} catch (Exception e) {
			logger.error("importBasketInfo error", e);
			return ResultUtil.fail("file is invaild.");
		}
		if (basketinfos == null || basketinfos.isEmpty()) {
			logger.error("import file is empty.");
			return ResultUtil.fail("import file is empty.");
		}
		logger.debug("importBasketInfo read PackageInfo from file number:{}", basketinfos.size());

		List<PackageInfo> addFailList = new ArrayList<>();
		String message = "Check package failded:\n";
		for (PackageInfo bsk : basketinfos) {
			bsk.setUserid(userid);
			logger.debug("importBasketInfo check :{}", bsk.getBasketno());

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
			logger.debug("importBasketInfo add :{}", bsk.getBasketno());
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

	/**
	 * 导出公共方法
	 * 
	 * @author shuhao.song
	 * @param request
	 * @param response
	 * @param step
	 *            步骤
	 * @return
	 * @throws IOException
	 *             获取step,设置status，查询导出list，调用封装csv接口，response set流进行下载。
	 */
	@RequestMapping(value = "/createExportData")
	@ResponseBody
	public String createExportData(HttpServletRequest request, HttpServletResponse response, String step) {
		String userid = (String) request.getSession().getAttribute(Constants.SESSION_USER_ID);
		logger.debug("createExportData userid: {},step: {}",userid,step);
		Map<String, ExportConfig> exportCfgMap = DiamondApplicationRunner.getExportConfig();
		if (!exportCfgMap.containsKey(step)) {
			logger.error("can not get export config :{}", step);
			return ResultUtil.fail("can not get export config");
		}
		
		String fileType= request.getParameter("fileType");
		fileType = ".xls";
		String filePath = "";
		Map<String, Object> result = new HashMap<String, Object>();
		ExportConfig cfg = exportCfgMap.get(step);
		String[] header = cfg.getHeader();
		String[] statusList = cfg.getStatus();
		try {
			if (step.equals(Constants.SUPPLIER_TO_AOC)) {
				List<DiamondInfoData> list = diamondsInfoService.getDiamondInfoByStatus(userid, statusList);
				logger.info("export diamond step:{} ,result size: {}",step,list.size());
				filePath = FileUtils.generateExportFile(tmpFilePath, diamondPrefix, header, list,fileType);
				logger.info("export diamond step:{} ,save to file: {}",step,filePath);
			} else {
				List<PackageInfo> list = null;
				if (step.equals(Constants.AOC_TO_AUDIT) || step.equals(Constants.AUDIT_TO_AOC)) {
					list = packageInfoService.getPackageStateWithoutRedeemByStatus(userid, redeemOwnerId, statusList);
				} else {
					list = packageInfoService.getPackageInfoByStatus(userid, statusList);
				}
				logger.info("export package step:{} ,result size: {}",step,list.size());
				filePath = FileUtils.generateExportFile(tmpFilePath, packagePrefix, header, list,fileType);
				logger.info("export package step:{} ,save to file: {}",step,filePath);
			}
			result.put("state", "success");
			result.put("filePath", filePath);
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			result = ResultUtil.failMap("export data error");
			logger.error("export data error " + step, e);
		}
		return JSON.toJSONString(result);
	}

	/**
	 * @author shuhao.song
	 * @param response
	 * @param filePath
	 *            文件路径 ，默认csv，后续如果需要在扩展
	 * @throws IOException
	 */
	@RequestMapping(value = "/downloadExportData")
	@ResponseBody
	public void downloadExportData(HttpServletResponse response, String filePath) throws IOException {
		String contentType = "application/octet-stream;charset=UTF-8";
		FileUtils.exportFile(response, filePath, contentType);
	}

}
