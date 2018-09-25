package ats.blockchain.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ats.blockchain.cordapp.diamond.data.DiamondsInfo;
import ats.blockchain.cordapp.diamond.data.PackageState;
import ats.blockchain.web.bean.DiamondInfoData;
import ats.blockchain.web.bean.PackageAndDiamond;
import ats.blockchain.web.bean.PackageInfo;
import ats.blockchain.web.corda.CordaApi;
import ats.blockchain.web.corda.impl.DiamondTradeApi;
import ats.blockchain.web.dao.BasketinfoMapper;
import ats.blockchain.web.dao.DiamondsinfoMapper;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.model.DiamondsinfoExample;
import ats.blockchain.web.model.PagedObjectDTO;
import ats.blockchain.web.servcie.DiamondsInfoService;
import ats.blockchain.web.utils.AOCBeanUtils;
import ats.blockchain.web.utils.Constants;
import ats.blockchain.web.utils.DateFormatUtils;
import ats.blockchain.web.utils.FileUtils;
import ats.blockchain.web.utils.ResultUtil;
import net.corda.core.contracts.StateAndRef;

@Controller
@RequestMapping("/diamond")
public class DiamondsInfoController2 extends BaseController {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DiamondsinfoMapper diamondsinfoMapper;
	@Autowired
	private BasketinfoMapper basketinfoMapper;
	@Resource(name="diamondsInfoServiceCordaImpl")
	private DiamondsInfoService diSvc;
	
	@Autowired
	private CordaApi cordaApi;
	private String aoc = "O=AOC,L=HKSAR,C=CN";

	@RequestMapping("/addDiamondInfo")
	@ResponseBody
	public String addDiamondInfo(DiamondInfoData diamondsinfo) {
		logger.debug("DiamondsInfoController:diamondsinfo---->" + diamondsinfo.toString());

		boolean rs  = diSvc.addDiamondInfo(diamondsinfo);
		return ResultUtil.msg(rs, "add diamond success.");
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
		List<DiamondInfoData> list = diSvc.getDiamondInfoByStatus(PackageState.DMD_CREATE,PackageState.DMD_ISSUE);
		PagedObjectDTO dto = new PagedObjectDTO();
		dto.setRows(list);
		dto.setTotal((long)list.size());
		return dto;
	}

	// @RequestMapping("/getDiamondList")
	// @ResponseBody
	public PagedObjectDTO getDiamondListServer(@RequestParam int pageNumber, int pageSize, HttpServletRequest request) {
		DiamondsinfoExample example = new DiamondsinfoExample();
		List<String> statusList = new ArrayList<String>();
		statusList.add(Constants.status_basket_submit);
		statusList.add(Constants.status_diamonds_into);
		PageHelper.startPage(pageNumber, pageSize);
		example.createCriteria().andStatusIn(statusList);
		List<Diamondsinfo> diamondsinfos = diamondsinfoMapper.selectByExample(example);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		PageInfo<Diamondsinfo> pageInfo = new PageInfo<>(diamondsinfos);
		if (diamondsinfos != null && !diamondsinfos.isEmpty()) {
			pagedObjectDTO.setRows(diamondsinfos);
			pagedObjectDTO.setTotal(Long.valueOf(pageInfo.getTotal()));
			return pagedObjectDTO;
		}
		return null;

	}

	@RequestMapping("/getDiamondListFromCorda")
	@ResponseBody
	public PagedObjectDTO getDiamondList2(@RequestParam int pageNumber, int pageSize, HttpServletRequest request) {
		DiamondTradeApi api = cordaApi.getTradediamondinf();
		logger.debug("getDiamondListFromCorda,query status: {}, {} .",PackageState.PKG_ISSUE,PackageState.DMD_CREATE);
		List<StateAndRef<PackageState>> list = api.getPackageStateByStatus(PackageState.PKG_ISSUE,PackageState.DMD_CREATE);
		
		logger.debug("getDiamondListFromCorda,query status: {}, {} ,result num:{}.",PackageState.PKG_ISSUE,PackageState.DMD_CREATE,list.size());
		List<DiamondInfoData> diamondList=   Lists.newArrayList();
		 List<PackageAndDiamond> padList = AOCBeanUtils.convertPakageState2PackageInfo(list);
		for(PackageAndDiamond pad :padList) {
			List<DiamondInfoData> dl = pad.getDiamondList();
			PackageInfo pkg = pad.getPkgInfo();
			if(dl!=null&& dl.size()>0) {
				logger.debug("package has diamond , add to result, basketno: {}, size: {}",pkg.getBasketno(),dl.size());
				diamondList.addAll(dl);
//				for(DiamondsInfo1 dc :dl) {
//					Diamondsinfo di = new Diamondsinfo();
//					BeanUtils.copyProperties(dc, di);
//					logger.debug("copy diamondsInfo1: {} ,diamondInfo: {}",dc,di);
//					diamondList.add(di);
//				}
			}else {
				int num = pkg.getDiamondsnumber();
				logger.debug("package has no diamond , convert PackageInfo to DiamondInfo, basketno: {}, size: {}",pkg.getBasketno(),num);
				for (int j = 0;j<num;j++) {
					DiamondInfoData di = new DiamondInfoData();
					BeanUtils.copyProperties(pkg, di);
					logger.debug("copy packagInfo: {} ,diamondInfo: {}",pkg,di);
					diamondList.add(di);
				}
			}
		}
		PagedObjectDTO dto  = new PagedObjectDTO();
		dto.setRows(diamondList);
		dto.setTotal((long)diamondList.size());
		return dto;

	}

	@RequestMapping("/submitDiamondList")
	@ResponseBody
	public String submitDiamondList(HttpServletRequest request) throws JSONException {
		logger.debug("submitDiamondList begin");
		// 1，查询需要提交的basket信息
		JSONObject result = new JSONObject();
		DiamondsinfoExample example = new DiamondsinfoExample();
		example.createCriteria().andStatusEqualTo(Constants.status_basket_submit);
		List<Diamondsinfo> list = diamondsinfoMapper.selectByExample(example);
		if (null != list && list.size() > 0) {
			String message = "these bakset is not fill to the full diamonds[";
			for (int i = 0; i < list.size(); i++) {
				if (message.indexOf(list.get(i).getBasketno()) == -1) {
					message = message + list.get(i).getBasketno() + ":";
				}

			}
			message = message + "]";
			result.put("state", "fail");
			result.put("message", message);
		} else {
			example.clear();
			example.createCriteria().andStatusEqualTo(Constants.status_diamonds_into);
			list = diamondsinfoMapper.selectByExample(example);
			try {

				if (null != list && list.size() > 0) {
					logger.debug("begin update db and corda status_diamonds_into :{}", list.size());
					// 2，调用cordaAPI进行区块链存储

					// 3，存储成功之后更新basket信息
					Map<String, String> basketNoMap = new HashMap<String, String>();
					Basketinfo basketinfo = new Basketinfo();
					Map<String, List<DiamondsInfo>> basketDiamondMap = new HashMap<String, List<DiamondsInfo>>();
					for(Diamondsinfo diamondsinfo : list)
					{
						diamondsinfo.setStatus(Constants.status_diamonds_sumbmit);
						diamondsinfoMapper.updateByPrimaryKey(diamondsinfo);
						if(!basketNoMap.containsKey(diamondsinfo.getBasketno()))
						{
							basketNoMap.put(diamondsinfo.getBasketno(), diamondsinfo.getBasketno());
							basketinfo.setBasketno(diamondsinfo.getBasketno());
							basketinfo.setStatus(diamondsinfo.getStatus());
							basketinfoMapper.updateByPrimaryKeySelective(basketinfo);
						}
						
					}
				}
				logger.debug("update  db and corda end");
				result.put("state", "success");
			} catch (Exception e) {
				logger.error("DiamondsInfoController:error：", e);
				result.put("state", "fail");
				result.put("message", e.getMessage().toString());
			}
		}
		return result.toString();
	}


	@RequestMapping(value = "/importDiamondsInfo")
	@ResponseBody
	public String importDiamondsInfo(HttpServletRequest request) throws JSONException {
		List<Diamondsinfo> diamondsinfos = null;
		String message = "";
		try {
			String filename = FileUtils.getFile(request, "diamondsinfo", "files");
			diamondsinfos = AOCBeanUtils.getObjectFromCsv(filename, Diamondsinfo.class);
		} catch (Exception e) {
			logger.error("DiamondsInfoController--->importDiamondsInfo：" + e.getMessage());
			e.printStackTrace();
		}

		JSONObject result = new JSONObject();
		try {
			if (null != diamondsinfos && diamondsinfos.size() > 0) {
				// 查询数据库basketinto
				DiamondsinfoExample example = new DiamondsinfoExample();
				example.createCriteria().andStatusEqualTo("1");
				List<Diamondsinfo> diamondsinfosDb = diamondsinfoMapper.selectByExample(example);
				List<Diamondsinfo> filerList = null;
				for (Diamondsinfo diamondsinfo : diamondsinfos) {
					filerList = diamondsinfosDb.stream()
							.filter((Diamondsinfo d) -> diamondsinfo.getBasketno().equals(d.getBasketno()))
							.collect(Collectors.toList());
					if (null == filerList || filerList.isEmpty()) {
						result.put("state", "fail");
						result.put("message",
								"There is no record in the database about[" + diamondsinfo.getBasketno() + "]");
						break;
					} else {
						diamondsinfo.setTradeid(filerList.get(0).getTradeid());
						diamondsinfo.setStatus("2");
						diamondsinfoMapper.updateByPrimaryKey(diamondsinfo);
						result.put("state", "success");
						result.put("message", message = message + "Import baksetinfo[" + diamondsinfo.getBasketno()
								+ "->giaNo:" + diamondsinfo.getGiano() + "]");
					}

				}
			} else {
				result.put("state", "fail");
				result.put("message", "There is no data to import");
			}

		} catch (Exception e) {
			logger.error("DiamondsInfoController--->importDiamondsInfo：" + e.getMessage());
			result.put("state", "fail");
			result.put("message", e.getMessage().toString());
		}
		return result.toString();
	}
	

	public Map<String, Object> getBasketMap() {
		 Map<String, Object>  basketMap = null;
		List<DiamondInfoData> list = diSvc.getDiamondInfoByStatus(PackageState.PKG_ISSUE);
		if(list!=null) {
//			basketMap = list.stream().collect(Collectors.toMap(Diamondsinfo::getBasketno,Diamondsinfo::getBasketno,(key1,key2)->key2));
			basketMap = new HashMap<String,Object>();
			for(DiamondInfoData l:list) {
				basketMap.put(l.getBasketno(), l.getBasketno());
			}
			logger.debug("getBasketMap :{}",basketMap);
		}else {
			logger.debug("basket is null");
			basketMap = Collections.emptyMap();
		}
			
		return basketMap;
	}

}
