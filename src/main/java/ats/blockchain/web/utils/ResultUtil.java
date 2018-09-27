package ats.blockchain.web.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class ResultUtil {

	public static String msg(boolean state,String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", state ? "success" : "fail");
		result.put("message", msg);
		return JSON.toJSONString(result);
	}
	
	public static String fail(String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "fail");
		result.put("message", msg);
		return JSON.toJSONString(result);
	}
	
	public static String fail(String msg,Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "fail");
		result.put("message", msg);
		result.put("data", data);
		return JSON.toJSONString(result);
	}
	private final static String success = "{\"state\":\"success\"}";
	public static String success() {
		return success;
	}
}
