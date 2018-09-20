package ats.blockchain.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import ats.blockchain.web.dao.BasketinfoMapper;
import ats.blockchain.web.dao.DiamondsinfoMapper;
import ats.blockchain.web.model.Basketinfo;
import ats.blockchain.web.model.BasketinfoExample;
import ats.blockchain.web.model.Diamondsinfo;
import ats.blockchain.web.model.DiamondsinfoExample;
import ats.blockchain.web.model.PagedObjectDTO;

@Controller
@RequestMapping("/history")
public class DiamondsHistoryController extends BaseController {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DiamondsinfoMapper diamondsinfoMapper;
	
	@Autowired
	private BasketinfoMapper basketinfoMapper;

	@RequestMapping("/historyList")
	public String findBasketList()
	{
		return "historyList";
	}
	
	@RequestMapping("/getDiamondList")
	@ResponseBody
	public PagedObjectDTO getDiamondListClient(@RequestParam int pageNumber, int pageSize, HttpServletRequest request) {
		DiamondsinfoExample example = new DiamondsinfoExample();
		List<Diamondsinfo> diamondsinfos = diamondsinfoMapper.selectByExample(example);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		if (diamondsinfos != null && !diamondsinfos.isEmpty()) {
			pagedObjectDTO.setRows(diamondsinfos);
			pagedObjectDTO.setTotal(Long.valueOf(diamondsinfos.size()));
			return pagedObjectDTO;
		}
		return null;

	}

	@RequestMapping("/getDiamondDetails")
	@ResponseBody
	public String getDiamondDetails(@RequestParam int pageNumber, int pageSize,String basketno) throws JSONException {
		DiamondsinfoExample example = new DiamondsinfoExample();
		example.createCriteria().andBasketnoEqualTo(basketno);
		List<Diamondsinfo> diamondsinfos = diamondsinfoMapper.selectByExample(example);
		Basketinfo basketinfo= basketinfoMapper.selectByPrimaryKey(basketno);
		JSONObject result = new JSONObject();
		if (diamondsinfos != null && !diamondsinfos.isEmpty()) {
			result.put("rows", JSON.toJSON(diamondsinfos));
			result.put("total", JSON.toJSON(diamondsinfos.size()));
			result.put("basketInfo", JSON.toJSON(basketinfo));
		}else
		{
			result.put("rows", JSON.toJSON(diamondsinfos));
			result.put("total", JSON.toJSON(diamondsinfos.size()));
			result.put("basketInfo", JSON.toJSON(new Basketinfo()));
		}
		return result.toString();
	}

	
	@RequestMapping("/getDiamondsHistoryList")
	@ResponseBody
	public PagedObjectDTO getDiamondsHistoryList(@RequestParam int pageNumber, int pageSize,String giano) {
		DiamondsinfoExample example = new DiamondsinfoExample();
		//example.createCriteria().andGianoEqualTo(giano);
		List<Diamondsinfo> diamondsinfos = diamondsinfoMapper.selectByExample(example);
		PagedObjectDTO pagedObjectDTO = new PagedObjectDTO();
		if (diamondsinfos != null && !diamondsinfos.isEmpty()) {
			pagedObjectDTO.setRows(diamondsinfos);
			pagedObjectDTO.setTotal(Long.valueOf(diamondsinfos.size()));
			return pagedObjectDTO;
		}
		return null;

	}
	// @RequestMapping("/getDiamondList")
	// @ResponseBody
	public PagedObjectDTO getDiamondListServer(@RequestParam int pageNumber, int pageSize, HttpServletRequest request) {
		DiamondsinfoExample example = new DiamondsinfoExample();
		PageHelper.startPage(pageNumber, pageSize);
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
}
